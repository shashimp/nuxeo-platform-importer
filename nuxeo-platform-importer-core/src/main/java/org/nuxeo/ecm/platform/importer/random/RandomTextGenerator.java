package org.nuxeo.ecm.platform.importer.random;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomTextGenerator {

    protected DictionaryHolder dictionaryHolder = new FrenchDictionaryHolder();

    protected Map<String, String> paragraphCache = new HashMap<String, String>();
    protected Map<String, String> pageCache = new HashMap<String, String>();
    protected Map<String, String> blockCache = new HashMap<String, String>();

    protected static final int PARAGRAPH_CACHE_SIZE = 100;
    protected static final int PARAGRAPH_CACHE_HIT = 100;
    protected static final int PAGE_CACHE_SIZE = 50;
    protected static final int PAGE_CACHE_HIT = 30;
    protected static final int BLOC_CACHE_SIZE = 30;
    protected static final int BLOC_CACHE_HIT = 20;
    protected static final int BLOC_SIZE = 10 * 1024;

    protected Random generator;

    public RandomTextGenerator() throws Exception {
        generator = new Random(System.currentTimeMillis());
    }

    public String getRandomLine() {
        int nbW = 10 + generator.nextInt(20);
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < nbW; i++) {
            sb.append(dictionaryHolder.getRandomWord());
        }
        sb.append(".\n");
        return sb.toString();
    }

    public String generateParagraph() {
        int nbL = 10 + generator.nextInt(40);
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < nbL; i++) {
            sb.append(getRandomLine());
        }
        sb.append("\n\n");
        return sb.toString();
    }

    public void prefilCache() throws Exception {

        dictionaryHolder.init();

        for (int i = 0; i < PARAGRAPH_CACHE_SIZE; i++) {
            paragraphCache.put("P" + i, generateParagraph());
        }

        for (int i = 0; i < PAGE_CACHE_SIZE; i++) {
            String page = generatePage();
            pageCache.put("P" + i, page);
        }

        for (int i = 0; i < BLOC_CACHE_SIZE; i++) {
            String page = generateBloc();
            blockCache.put("B" + i, page);
        }

    }

    public String getRandomParagraph() {
        int rand = generator.nextInt();
        int idx = generator.nextInt(PARAGRAPH_CACHE_SIZE);
        String paragraph = null;
        if (rand % PARAGRAPH_CACHE_HIT != 0) {
            paragraph = paragraphCache.get("P" + idx);
        }
        if (paragraph == null) {
            paragraph = generateParagraph();
            paragraphCache.put("P" + idx, paragraph);
        }
        return paragraph;
    }

    public String generatePage() {
        int nbL = generator.nextInt(8) + 1;
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < nbL; i++) {
            sb.append(getRandomParagraph());
        }
        sb.append("\n\n");
        return sb.toString();
    }

    public String getRandomPage() {
        int rand = generator.nextInt();
        int idx = generator.nextInt(PAGE_CACHE_SIZE);
        String page = null;
        if (rand % PAGE_CACHE_HIT != 0) {
            page = pageCache.get("P" + idx);
        }
        if (page == null) {
            page = generatePage();
            pageCache.put("P" + idx, page);
        }
        return page;
    }

    public String generateBloc() {
        StringBuffer sb = new StringBuffer();

        while (sb.length() < BLOC_SIZE) {
            sb.append(getRandomPage());
        }
        return sb.toString();
    }

    public String getRandomBloc() {
        int rand = generator.nextInt();
        int idx = generator.nextInt(BLOC_CACHE_SIZE);
        String bloc = null;
        if (rand % BLOC_CACHE_HIT != 0) {
            bloc = blockCache.get("B" + idx);
        }
        if (bloc == null) {
            bloc = generateBloc();
            blockCache.put("B" + idx, bloc);
        }
        return bloc;
    }

    public String getRandomText(int avSizeInK) {
        StringBuffer sb = new StringBuffer();
        int minSize = (int) (avSizeInK * 1024 * (0.8 + 0.4 * generator
                .nextFloat()));
        while (sb.length() < (minSize - BLOC_SIZE)) {
            String p = getRandomBloc();
            sb.append(p);
        }
        while (sb.length() < minSize) {
            String p = getRandomPage();
            sb.append(p);
        }
        return sb.toString();
    }

    public String getRandomText() {
        int sizeK = generator.nextInt(500) + 1;
        return getRandomText(sizeK);
    }

}
