package ems.model;

public class Address {
    private String street, city, state, zip;

    public Address() { }
    public Address(String street, String city, String state, String zip) {
        this.street = street; this.city = city; this.state = state; this.zip = zip;
    }
    public String getStreet() { return street; }
    public void setStreet(String v) { street = v; }
    public String getCity() { return city; }
    public void setCity(String v) { city = v; }
    public String getState() { return state; }
    public void setState(String v) { state = v; }
    public String getZip() { return zip; }
    public void setZip(String v) { zip = v; }

    /** True when no part of the address was entered. */
    public boolean isEmpty() { return blank(street) && blank(city) && blank(state) && blank(zip); }
    private static boolean blank(String s) { return s == null || s.isBlank(); }

    @Override public String toString() { return street + ", " + city + ", " + state + " " + zip; }
}
