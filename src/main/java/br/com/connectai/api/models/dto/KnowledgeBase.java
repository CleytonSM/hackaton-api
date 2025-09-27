package br.com.connectai.api.models.dto;

import java.util.List;

public class KnowledgeBase {

    private List<QAPair> qaPairs;

    public KnowledgeBase() {}

    public KnowledgeBase(List<QAPair> qaPairs) {
        this.qaPairs = qaPairs;
    }

    public List<QAPair> getQaPairs() {
        return qaPairs;
    }

    public void setQaPairs(List<QAPair> qaPairs) {
        this.qaPairs = qaPairs;
    }

    public static class QAPair {
        private String question;
        private String answer;
        private List<String> keywords;

        public QAPair() {}

        public QAPair(String question, String answer, List<String> keywords) {
            this.question = question;
            this.answer = answer;
            this.keywords = keywords;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<String> keywords) {
            this.keywords = keywords;
        }
    }
}
