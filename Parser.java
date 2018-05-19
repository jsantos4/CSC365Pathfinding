package YelpDataPathfinding;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class Parser {

    static ArrayList parse(String file, ArrayList<String> strings) throws IOException {
        //Create file reader
        ArrayList<Node> businesses = new ArrayList<>();
        ArrayList<String> cat = new ArrayList<>();
        BufferedReader bufRead = new BufferedReader(new FileReader(file));
        String line;

        //Run infinite loop
        //while (true) {
        for (int i = 0; i < 1000; i++) {
            //Read line of file
            line = bufRead.readLine();

            //If line is not empty
            if (line != null) {
                //New json object
                org.json.JSONObject jsonObject = new org.json.JSONObject(line);

                //Pull relevant data from json line
                Business business = new Business(jsonObject.get("business_id").toString(), jsonObject.get("name").toString(),
                        jsonObject.get("city").toString(), jsonObject.get("state").toString());

                //System.out.println(i);
                business.setLocation(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"));

                //Create list of categories from file
                ArrayList<String> categoryCollection = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(jsonObject.getJSONArray("categories").toString());

                //Add categories from json to category list in business object
                for (int j = 0; j < jsonArray.length(); j++) {
                    categoryCollection.add(jsonArray.get(j).toString());
                }
                business.categories.addAll(categoryCollection);

                for (String c : business.categories) {
                    if (!cat.contains(c))
                        cat.add(c);
                }

                //Add business to Array list
                strings.add(business.toString());
                businesses.add(new Node(business));
                //if line is empty then EOF reached, let user know and break loop
            } else {
                System.out.println("Done");
                break;
            }
        }

        //Give each business it's category score (value used for clustering)
        for (Node b : businesses) {
            for (String c : b.business.categories) {
                b.business.categoryScore += cat.indexOf(c);
                b.business.categoryScore = b.business.categoryScore/b.business.categories.size();
            }
        }

        return businesses;
    }
}


