package us.ar.com.tecnoap.scanner;

/**
 * Created by apple on 11/28/17.
 */

public class Model {

    String code,name,price,date;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Model(String code, String name, String price, String date) {

        this.code = code;
        this.name = name;
        this.price = price;
        this.date = date;
    }
}
