package ro.demo;

import java.util.Date;

/**
 * Created by sorin on 14.06.2016.
 */
public class PersonResource {
int id;
    String cnp;
    Date dateOfBirth;

    public PersonResource() {
    }

    public PersonResource(int id, String cnp) {
        this.id = id;
        this.cnp = cnp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
