package com.company.econatia.Model;

public class Product {

    private String productName;
    private String productId;
    private String getProductDesc;
    private String productCost;
    private String productImgUrl;

    public Product(String productName, String getProductDesc, String productCost, String productImgUrl , String productId) {
        this.productName = productName;
        this.getProductDesc = getProductDesc;
        this.productCost = productCost;
        this.productImgUrl = productImgUrl;
        this.productId = productId;
    }

    public Product() {
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getGetProductDesc() {
        return getProductDesc;
    }

    public void setGetProductDesc(String getProductDesc) {
        this.getProductDesc = getProductDesc;
    }

    public String getProductCost() {
        return productCost;
    }

    public void setProductCost(String productCost) {
        this.productCost = productCost;
    }

    public String getProductImgUrl() {
        return productImgUrl;
    }

    public void setProductImgUrl(String productImgUrl) {
        this.productImgUrl = productImgUrl;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
