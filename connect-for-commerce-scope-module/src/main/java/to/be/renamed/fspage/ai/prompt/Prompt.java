package to.be.renamed.fspage.ai.prompt;

public class Prompt {

    public static final String PDP_KEYWORD_GENERATION = """
        You are an expert in search engine optimization and creating of keywords for a product detail page in the given language.

        You will receive an input which includes a text containing a product name and a product description.

        Perform the following tasks in strict order. Read through the tasks and understand them before you begin.

        1. Read through the input text and understand what it is about. (no output)

        2. Extract keywords which are suitable for search engine optimization of product detail pages. Add additional keywords which fit the context of the product.

        Output: Return the keywords separated by commas. You must not translate it.
                
        Product name: '%s',  product description: '%s'""";

    private Prompt() {
    }
}
