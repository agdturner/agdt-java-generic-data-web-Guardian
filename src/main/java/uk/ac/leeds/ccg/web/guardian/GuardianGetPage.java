/*
 * Copyright 2017 Andy Turner, University of Leeds.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.leeds.ccg.web.guardian;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import uk.ac.leeds.ccg.data.core.Data_Environment;
import uk.ac.leeds.ccg.data.format.Data_ReadTXT;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.generic.io.Generic_IO;
import uk.ac.leeds.ccg.web.core.Web_Environment;
import uk.ac.leeds.ccg.web.core.Web_Object;

/**
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class GuardianGetPage extends Web_Object {

    public final Data_ReadTXT reader;

    GuardianGetPage(Web_Environment e) {
        super(e);
        reader = new Data_ReadTXT(e.de);
    }

    public static void main(String[] args) {
        try {
            GuardianGetPage p = new GuardianGetPage(new Web_Environment(
                    new Data_Environment(new Generic_Environment(
                            new Generic_Defaults()))));
            p.run();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void run() throws IOException {
        Path dataDir;
        dataDir = Paths.get(System.getProperty("user.dir"), "data/");
        String filename;
        filename = "LexisNexis - The Guardian - Refugees AND BrexitHeadlinesForArticlesContaining_Syria.csv";
        run(dataDir, filename);
        filename = "LexisNexis - The Guardian - RefugeesHeadlinesForArticlesContaining_Syria.csv";
        run(dataDir, filename);
    }

    void run(Path dataDir, String filename) throws IOException {
        Path inDir = Paths.get(dataDir.toString(), "input", "LexisNexis");
        Path fin = Paths.get(inDir.toString(), filename);
        Path outDir = Paths.get(dataDir.toString(), "output", "LexisNexis");
        Path fout = Paths.get(outDir.toString(), filename);
        Path fout2 = Paths.get(dataDir.toString(), filename + "del");
        try (PrintWriter pw = Generic_IO.getPrintWriter(fout, false)) {
            String GuardianAPIKey;
            GuardianAPIKey = getGuardianAPIKey(dataDir);
            
            ArrayList<String> lines = reader.read(fin, outDir, 6);
            
            String s;
            for (int i = 0; i < lines.size(); i++) {
                if (i == 0) {
                    pw.println(lines.get(i));
                    System.out.println(lines.get(i));
                } else {
                    String[] vals;
                    vals = lines.get(i).split(",\"");
                    for (int j = 0; j < vals.length; j++) {
                        if (j != 1) {
                            if (j != vals.length - 1) {
                                s = vals[j] + ",\"";
                                pw.print(s);
                                System.out.print(s);
                            } else {
                                pw.println(vals[j]);
                                System.out.println(vals[j]);
                            }
                        } else {
                            String sf = vals[vals.length - 1];
                            String title = sf.substring(0, sf.length() - 2);
                            String page;
                            page = getPage(vals[0], title, GuardianAPIKey, fout2);
                            s = vals[j].replace("\"", "") + " " + page + "\",\"";
                            pw.print(s);
                            System.out.print(s);
                        }
                    }
                }
            }
        }
    }

    String getPage(String date, String Title, String GuardianAPIKey, Path f) 
            throws IOException {
//        String Title;
//        Title = "Terror threats will be the new normal for Europe, experts say; \n"
//                + "Analysts believe there will be more security alerts and cancellations of major events after Paris attacks";
//        Title = "The Brexit nightmare is becoming reality. The remain camp is in denial; From Cameron's Panama Papers debacle to the weakness of Merkel and Hollande, the omens for Britain remaining in the EU get poorer by the day. Does anyone care?";
        String title;
        title = Title.replaceAll("\n", "-");
        title = title.replaceAll(" ", "-");
        title = title.replaceAll("'", "");
        title = title.replaceAll(",", "");
        title = title.replaceAll("\\.", "");
        title = title.replaceAll(";", "");
        title = title.replaceAll(":", "");
        title = title.replaceAll("\\?", "");
        title = title.replaceAll("!", "");
        title = title.replaceAll("--", "-");
        title = title.replaceAll("--", "-");
        title = title.replaceAll("--", "-");
        title = title.replaceAll("--", "-");

        //System.out.println(title);
//        //String date = "2016-04-09";
//        String[] dateParts = date.split("/");
//        String date2 = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
        //newspaperPageNumber,newspaperEditionDate
        String url;
        url = "http://content.guardianapis.com/search?"
                + "from-date=" + date + "&to-date=" + date
                + "&show-fields=newspaperPageNumber%2CnewspaperEditionDate&q="
                + title + "&api-key=" + GuardianAPIKey;
//        Path f;
//        f = Paths.get(outputDataDir, "GuardianGetPage.html");
        ArrayList<String> html = getHTML(url, f);
        if (html.size() > 0) {
            String[] split = html.get(0).split("\"newspaperPageNumber\":");
            if (split.length > 1) {
                String[] split2 = split[1].split("\"");
                //System.out.println("Page " + split2[1]);
                return "Page " + split2[1];
            }
        }
        return "";
    }

    public ArrayList<String> getHTML(String sURL, Path fileToStore)
            throws IOException {
        ArrayList<String> r = new ArrayList<>();
        try (PrintWriter pw = Generic_IO.getPrintWriter(fileToStore, false)) {
            URL url = new URL(sURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                pw.println(line);
                r.add(line);
            }
        }
        return r;
    }

    String getGuardianAPIKey(Path dataDir) throws FileNotFoundException, IOException {
        String r = "";
        Path dir  = Paths.get(dataDir.toString(), "private");
        Path f = Paths.get(dir.toString(), "GuardianAPIKey.txt");
        try (BufferedReader br = Generic_IO.getBufferedReader(f)) {
            r = br.readLine();
        }
        return r;
    }
}
