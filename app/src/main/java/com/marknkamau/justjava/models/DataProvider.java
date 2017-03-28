package com.marknkamau.justjava.models;

import java.util.ArrayList;
import java.util.List;

public class DataProvider {
    public static final List<CoffeeDrink> drinksList;

    static {
        drinksList = new ArrayList<>();

        addItem(new CoffeeDrink("0", "Cappuccino", "Foam, milk and espresso.",
                "A single, perfectly extracted shot of espresso is marbled with freshly steamed " +
                        "milk to create this coffeehouse staple.",
                "450", "cappuccino.jpg"));
        addItem(new CoffeeDrink("1", "Americano", "Water and espresso.",
                "Italian espresso gets the American treatment; hot water fills the cup for a rich " +
                        "alternative to drip coffee.",
                "300", "americano.jpg"));
        addItem(new CoffeeDrink("2", "Espresso", "Just a shot of espresso.",
                "Our beans are deep roasted, our shots hand-pulled. Taste the finest, freshly " +
                        "ground espresso shot in town.",
                "350", "espresso.jpg"));
        addItem(new CoffeeDrink("3", "Macchiato", "Foam and Espresso.",
                "What could top a hand-pulled shot of our richest, freshest espresso? A dollop " +
                        "of perfectly steamed foam.",
                "400", "macchiato.jpg"));
        addItem(new CoffeeDrink("4", "Mocha", "Milk, chocolate syrup and espresso.",
                "A marriage made in heaven. Espresso, steamed milk and our finest Dutch Cocoa. " +
                        "Whipped cream officiates.",
                "550", "mocha.jpg"));
        addItem(new CoffeeDrink("5", "Latte", "Milk and espresso.",
                "This coffee house favorite adds silky steamed milk to shots of rich espresso, " +
                        "finished with a layer of foam.",
                "450", "latte.jpg"));
        addItem(new CoffeeDrink("6", "Iced Coffee", "Milk, espresso and ice.",
                "Rich espresso, sweetened condensed milk, and cold milk poured over ice.",
                "500", "iced-coffee.jpg"));
        addItem(new CoffeeDrink("7", "Cafe frappe", "Ice cream, espresso topped with whipped cream.",
                "Rich espresso and Dutch Cocoa are pulled, then poured over ice cream for true " +
                        "refreshment. Topped with whipped cream.",
                "600", "cafe-frappe.jpg"));
    }

    private static void addItem(CoffeeDrink drink) {
        drinksList.add(drink);
    }
}
