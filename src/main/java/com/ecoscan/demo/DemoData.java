package com.ecoscan.demo;

import static com.ecoscan.constant.WasteConstants.*;

import com.ecoscan.model.DemoItem;
import com.ecoscan.model.WasteResult;
import java.util.List;
import java.util.Map;

public final class DemoData {

    public static final List<DemoItem> ITEMS = List.of(
            new DemoItem("plastic-bottle", "Plastic Bottle", "🥤"),
            new DemoItem("aluminum-can", "Aluminum Can", "🥫"),
            new DemoItem("banana-peel", "Banana Peel", "🍌"),
            new DemoItem("paper", "Paper", "📄"),
            new DemoItem("glass-bottle", "Glass Bottle", "🍾"),
            new DemoItem("food-waste", "Food Waste", "🍲"),
            new DemoItem("plastic-bag", "Plastic Bag", "🛍️"),
            new DemoItem("battery", "Battery", "🔋"),
            new DemoItem("cardboard", "Cardboard", "📦"));

    public static final Map<String, WasteResult> RESULTS = Map.ofEntries(
            Map.entry("plastic-bottle", result(
                    "Plastic Bottle", CATEGORY_PLASTIC, BIN_RECYCLABLE,
                    "Rinse it and recycle it if accepted locally.",
                    "PET 1 Plastic", RECYCLABILITY_EASY,
                    List.of("Empty all liquid", "Crush it if local rules allow", "Keep the cap on if accepted"),
                    "Up to 450 years",
                    "Plastic can break into tiny pieces that move through waterways.",
                    List.of("Keeps plastic out of landfills", "Provides material for new products"),
                    List.of("Can litter land and water", "Can spoil other recyclables"),
                    List.of("New bottles", "Polyester fabric"),
                    List.of("Use it as a small planter", "Use it to store craft items"),
                    "PET bottles can be made into new bottles.",
                    "Leaving liquid inside can spoil paper in the same bin.")),
            Map.entry("aluminum-can", result(
                    "Aluminum Can", CATEGORY_METAL, BIN_RECYCLABLE,
                    "Empty and rinse it before recycling.",
                    "Aluminum", RECYCLABILITY_EASY,
                    List.of("Empty the can", "Rinse away leftover liquid", "Check local sorting rules"),
                    "Can remain for many decades",
                    "Recycling aluminum helps keep useful metal in circulation.",
                    List.of("Saves valuable raw material", "Supports making new metal products"),
                    List.of("Litter can harm wildlife", "Mixed waste is harder to sort"),
                    List.of("New cans", "Metal parts"),
                    List.of("Use it as a pencil holder", "Make a small craft container"),
                    "Aluminum can be recycled many times.",
                    "Do not place cans with liquid still inside.")),
            Map.entry("banana-peel", result(
                    "Banana Peel", CATEGORY_ORGANIC, BIN_ORGANIC,
                    "Add it to an approved compost bin.",
                    "Organic Waste", RECYCLABILITY_EASY,
                    List.of("Put it in the compost bin", "Remove any plastic stickers"),
                    "A few weeks in suitable compost",
                    "Composting returns plant nutrients to soil.",
                    List.of("Makes useful compost", "Keeps food scraps from general waste"),
                    List.of("Food waste in landfills can create methane", "Pests may gather around exposed scraps"),
                    List.of("Compost for gardens", "Soil conditioner"),
                    List.of("Add small pieces to home compost", "Use in a garden compost pile"),
                    "Banana peels contain nutrients that compost can return to soil.",
                    "Do not wrap the peel in a plastic bag.")),
            Map.entry("paper", result(
                    "Paper", CATEGORY_PAPER, BIN_RECYCLABLE,
                    "Keep it clean and dry for recycling.",
                    "Paper Fiber", RECYCLABILITY_EASY,
                    List.of("Keep it dry", "Remove food or plastic", "Flatten it if needed"),
                    "A few weeks to several months",
                    "Clean paper can be turned into useful paper products.",
                    List.of("Saves fiber resources", "Reduces paper sent to landfill"),
                    List.of("Wet paper is harder to sort", "Grease can contaminate clean paper"),
                    List.of("New paper", "Cardboard products"),
                    List.of("Use blank sides for notes", "Reuse it for wrapping"),
                    "Paper fibers can be reused to make more paper.",
                    "Greasy or wet paper may not belong in recycling.")),
            Map.entry("glass-bottle", result(
                    "Glass Bottle", CATEGORY_GLASS, BIN_RECYCLABLE,
                    "Rinse it and recycle it where accepted.",
                    "Glass", RECYCLABILITY_EASY,
                    List.of("Empty and rinse it", "Keep broken glass separate", "Check local glass rules"),
                    "Can last for thousands of years",
                    "Recycling glass keeps reusable material in circulation.",
                    List.of("Creates material for new glass", "Reduces glass in landfill"),
                    List.of("Broken glass can injure workers", "Loose glass can be difficult to sort"),
                    List.of("New glass containers", "Glass construction material"),
                    List.of("Use it as a vase", "Store dry household items"),
                    "Glass can be recycled without losing its basic quality.",
                    "Do not put drinking glasses with bottle glass unless accepted.")),
            Map.entry("food-waste", result(
                    "Food Waste", CATEGORY_ORGANIC, BIN_ORGANIC,
                    "Put food scraps in an approved compost bin.",
                    "Organic Food Waste", RECYCLABILITY_EASY,
                    List.of("Use the food waste bin", "Remove wrappers and stickers", "Follow local compost rules"),
                    "A few weeks to several months in compost",
                    "Composting food scraps can return nutrients to the soil.",
                    List.of("Produces useful compost", "Keeps food scraps out of general waste"),
                    List.of("Landfilled food can release methane", "Loose scraps may attract pests"),
                    List.of("Compost for gardens", "Soil conditioner"),
                    List.of("Compost suitable scraps at home", "Plan meals to reduce leftovers"),
                    "Many fruit and vegetable scraps can be composted.",
                    "Do not mix plastic packaging into food compost.")),
            Map.entry("plastic-bag", result(
                    "Plastic Bag", CATEGORY_PLASTIC, BIN_NON_RECYCLABLE,
                    "Reuse it or place it in general waste.",
                    "Thin Plastic Film", RECYCLABILITY_HARD,
                    List.of("Reuse the bag when safe", "Keep it out of curbside recycling", "Follow local drop-off rules"),
                    "Can persist for many decades",
                    "Light plastic bags can escape into streets and waterways.",
                    List.of("Reuse reduces demand for new bags", "Proper disposal helps limit litter"),
                    List.of("Can entangle animals", "Can block sorting equipment"),
                    List.of("Some programs make plastic lumber", "Some programs make new film"),
                    List.of("Reuse it for shopping", "Use it to line a small bin"),
                    "Some stores collect clean bags for special recycling.",
                    "Loose bags can jam common recycling machinery.")),
            Map.entry("battery", result(
                    "Battery", CATEGORY_E_WASTE, BIN_SPECIAL,
                    "Take it to an approved battery collection point.",
                    "Household Battery", RECYCLABILITY_MEDIUM,
                    List.of("Keep it out of household bins", "Cover exposed terminals if advised",
                            "Use an approved collection point"),
                    "Can remain for many decades if discarded",
                    "Battery chemicals and metals need careful handling.",
                    List.of("Supports safe material recovery", "Reduces risk of fires in waste handling"),
                    List.of("Can leak harmful materials", "May start fires if damaged"),
                    List.of("Recovered metals", "Materials for new batteries"),
                    List.of("Use rechargeable batteries when suitable",
                            "Store used batteries safely until drop-off"),
                    "Some battery materials can be recovered for reuse.",
                    "Never place loose batteries in regular recycling bins.")),
            Map.entry("cardboard", result(
                    "Cardboard", CATEGORY_PAPER, BIN_RECYCLABLE,
                    "Flatten it and keep it clean and dry.",
                    "Corrugated Paperboard", RECYCLABILITY_EASY,
                    List.of("Flatten the box", "Remove plastic liners", "Keep it clean and dry"),
                    "A few months in suitable conditions",
                    "Recycling cardboard helps keep paper fibers in use.",
                    List.of("Provides fiber for new products", "Reduces bulky landfill waste"),
                    List.of("Wet cardboard is harder to recycle", "Food residue can contaminate paper"),
                    List.of("New cardboard", "Paper packaging"),
                    List.of("Reuse boxes for storage", "Use clean pieces for crafts"),
                    "Corrugated cardboard has a wavy middle layer for strength.",
                    "Do not recycle cardboard soaked with food or grease.")));

    private DemoData() {
    }

    private static WasteResult result(
            String itemName,
            String category,
            String bin,
            String tip,
            String material,
            String recyclability,
            List<String> howToPrepare,
            String decompositionTime,
            String whyItMatters,
            List<String> prosOfRightDisposal,
            List<String> consOfWrongDisposal,
            List<String> afterRecyclingItBecomes,
            List<String> reuseIdeas,
            String funFact,
            String commonMistake) {
        return new WasteResult(
                itemName, category, bin, tip, material, recyclability, howToPrepare, decompositionTime, whyItMatters,
                prosOfRightDisposal, consOfWrongDisposal, afterRecyclingItBecomes, reuseIdeas, funFact, commonMistake);
    }
}
