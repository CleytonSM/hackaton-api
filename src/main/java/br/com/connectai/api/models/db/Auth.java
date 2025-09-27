package br.com.connectai.api.models.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_auth")
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "auth_name", length = 15)
    private String authName;

    public Auth() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getAuthName() { return authName; }
    public void setAuthName(String authName) { this.authName = authName; }
}
