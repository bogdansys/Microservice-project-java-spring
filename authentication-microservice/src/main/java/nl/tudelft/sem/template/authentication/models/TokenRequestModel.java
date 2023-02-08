package nl.tudelft.sem.template.authentication.models;

public class TokenRequestModel {
    private String token;

    public TokenRequestModel() {
    }

    public TokenRequestModel(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
