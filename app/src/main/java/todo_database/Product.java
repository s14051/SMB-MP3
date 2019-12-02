package todo_database;

public class Product {
    private String productId;
    private String name;
    private double price;
    private int count;
    private boolean bought;

    public Product() {
    }

    public Product(String productId, String name, double price, int count, boolean bought) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.count = count;
        this.bought = bought;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean getBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", count=" + count +
                ", bought=" + bought +
                '}';
    }
}
