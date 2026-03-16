import java.util.*;

class PlagiarismDetector {

    // n-gram -> set of document IDs containing that n-gram
    private HashMap<String, Set<String>> ngramIndex = new HashMap<>();

    // documentId -> list of its ngrams
    private HashMap<String, List<String>> documentNgrams = new HashMap<>();

    private int N = 5; // 5-gram window

    // Break text into n-grams
    private List<String> generateNgrams(String text) {

        List<String> ngrams = new ArrayList<>();
        String[] words = text.toLowerCase().split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < N; j++) {
                sb.append(words[i + j]).append(" ");
            }

            ngrams.add(sb.toString().trim());
        }

        return ngrams;
    }

    // Add document to index
    public void addDocument(String documentId, String text) {

        List<String> ngrams = generateNgrams(text);
        documentNgrams.put(documentId, ngrams);

        for (String ngram : ngrams) {

            ngramIndex.putIfAbsent(ngram, new HashSet<>());
            ngramIndex.get(ngram).add(documentId);
        }
    }

    // Analyze document similarity
    public void analyzeDocument(String documentId) {

        List<String> ngrams = documentNgrams.get(documentId);

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String ngram : ngrams) {

            if (ngramIndex.containsKey(ngram)) {

                for (String doc : ngramIndex.get(ngram)) {

                    if (!doc.equals(documentId)) {

                        matchCount.put(doc,
                                matchCount.getOrDefault(doc, 0) + 1);
                    }
                }
            }
        }

        System.out.println("Analyzing Document: " + documentId);
        System.out.println("Total n-grams extracted: " + ngrams.size());

        for (String doc : matchCount.keySet()) {

            int matches = matchCount.get(doc);
            double similarity = (matches * 100.0) / ngrams.size();

            System.out.println("Matched with " + doc +
                    " → " + matches + " matching n-grams");

            System.out.println("Similarity: " +
                    String.format("%.2f", similarity) + "%");

            if (similarity > 60) {
                System.out.println("⚠ HIGH PLAGIARISM DETECTED");
            } else if (similarity > 20) {
                System.out.println("⚠ Moderate similarity detected");
            }

            System.out.println();
        }
    }
}

public class Week1andWeek2Problems {

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        String essay1 = "Artificial intelligence is transforming modern technology and improving automation in many industries";

        String essay2 = "Artificial intelligence is transforming modern technology and improving automation across many sectors";

        String essay3 = "Climate change is one of the most critical environmental challenges faced by humanity today";

        detector.addDocument("essay_A.txt", essay1);
        detector.addDocument("essay_B.txt", essay2);
        detector.addDocument("essay_C.txt", essay3);

        detector.analyzeDocument("essay_B.txt");
    }
}
