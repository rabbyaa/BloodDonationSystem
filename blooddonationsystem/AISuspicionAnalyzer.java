package blooddonationsystem;

import java.util.*;

/**
 * AI Suspicion Analyzer
 * Rule-based engine that scores each BloodRecord for suspicious patterns.
 * Score 0-100: 0=safe, 100=highly suspicious.
 * No external API needed — pure logic runs offline.
 */
public class AISuspicionAnalyzer {

    public static void analyze(BloodRecord record, List<BloodRecord> allRecords) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        String msg     = record.getMessage().toLowerCase();
        String name    = record.getUserName().toLowerCase();
        String phone   = record.getUserPhone().replaceAll("[^0-9]", "");
        String hospital= record.getHospital().toLowerCase();
        String location= record.getLocation().toLowerCase();

        // ── Rule 1: Urgency manipulation words ──────────────────────────────
        String[] urgencyWords = {"immediately", "emergency", "dying", "critical",
                                 "life or death", "last chance", "please help now",
                                 "right now", "today only", "urgent urgent"};
        int urgencyHits = 0;
        for (String w : urgencyWords) {
            if (msg.contains(w)) urgencyHits++;
        }
        if (urgencyHits >= 3) {
            score += 25;
            reasons.add("Excessive urgency language (" + urgencyHits + " flags)");
        } else if (urgencyHits >= 1) {
            score += 8;
            reasons.add("Urgency keywords detected");
        }

        // ── Rule 2: Money / payment mentions ────────────────────────────────
        String[] moneyWords = {"pay", "payment", "reward", "cash", "money",
                               "pkr", "rupees", "fee", "charge", "compensation"};
        for (String w : moneyWords) {
            if (msg.contains(w)) {
                score += 30;
                reasons.add("Monetary terms found: '" + w + "'");
                break;
            }
        }

        // ── Rule 3: Vague or missing hospital info ───────────────────────────
        if (hospital.length() < 4 || hospital.equals("n/a") || hospital.equals("none")
                || hospital.equals("unknown") || hospital.isEmpty()) {
            score += 20;
            reasons.add("Hospital name missing or vague");
        }

        // ── Rule 4: Suspicious phone number patterns ─────────────────────────
        if (phone.length() < 10) {
            score += 15;
            reasons.add("Invalid phone number length");
        } else if (phone.matches("(.)\\1{6,}")) {
            // e.g. 1111111111
            score += 20;
            reasons.add("Repeated digit phone number");
        }

        // ── Rule 5: Duplicate requests by same user ──────────────────────────
        long sameUserSameType = allRecords.stream()
            .filter(r -> r.getUserId().equals(record.getUserId())
                      && r.getType() == record.getType()
                      && !r.getRecordId().equals(record.getRecordId())
                      && r.getStatus() == BloodRecord.Status.PENDING)
            .count();
        if (sameUserSameType >= 3) {
            score += 25;
            reasons.add("User has " + sameUserSameType + " other pending same-type records");
        } else if (sameUserSameType >= 1) {
            score += 10;
            reasons.add("User already has pending " + record.getTypeLabel().toLowerCase());
        }

        // ── Rule 6: Too-short message (low effort = less verifiable) ─────────
        if (record.getMessage().trim().length() < 20) {
            score += 15;
            reasons.add("Message too short to verify");
        }

        // ── Rule 7: Name looks like placeholder ──────────────────────────────
        String[] fakeName = {"test", "asdf", "xxxx", "abc", "user", "admin", "dummy"};
        for (String fn : fakeName) {
            if (name.contains(fn)) {
                score += 20;
                reasons.add("Name looks like a placeholder");
                break;
            }
        }

        // ── Rule 8: Location vagueness ───────────────────────────────────────
        if (location.length() < 4 || location.equals("n/a") || location.equals("somewhere")) {
            score += 10;
            reasons.add("Location is vague or missing");
        }

        // ── Cap score ────────────────────────────────────────────────────────
        score = Math.min(score, 100);

        // ── Build reason string ──────────────────────────────────────────────
        String reasonText = reasons.isEmpty()
            ? "No suspicious patterns detected."
            : String.join(" | ", reasons);

        record.setAiSuspicionScore(score);
        record.setAiReason(reasonText);
        FileStorageManager.updateRecord(record);
    }

    public static String getRiskColor(int score) {
        if (score >= 70) return "#E53935"; // red
        if (score >= 40) return "#FB8C00"; // orange
        return "#43A047";                   // green
    }
}
