package fhu.wikialarmclock;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Alan on 2/26/2017.
 */

public class loadArticle extends IntentService
{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public loadArticle(String name)
    {
        super(name);
    }
    public loadArticle()
    {
        super("man");
    }

    @Override
    protected void onHandleIntent(Intent workIntent)
    {

        //random
        String entStr = "";
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;
        String fetchedUrl = "";
        try
        {
            fetchedUrl = getFinalURL("https://en.wikipedia.org/wiki/Special:Random");
            System.out.println(fetchedUrl);
            url = new URL (fetchedUrl);
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null)
            {
                entStr = entStr + line;
            }
        }
        catch (MalformedURLException mue)
        {
            mue.printStackTrace();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        finally
        {
            try
            {
                if (is != null) is.close();
            }
            catch (IOException ioe)
            {
            }
        }

        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("some_name", 0);
        SharedPreferences.Editor editor = mPrefs.edit();


        if (entStr.equals(""))
        {
            Set<String> set= new HashSet<String>();
            set.add("");
            editor.putStringSet("wikipedia title", set);

            Set<String> set1= new HashSet<String>();
            set1.add("");
            editor.putStringSet("wikipedia body", set1);

            Set<String> set2= new HashSet<String>();
            set2.add("");
            editor.putStringSet("wikipedia url", set2);

            Set<String> set4= new HashSet<String>();
            set4.add("");
            editor.putStringSet("featured title", set4);

            Set<String> set5 = new HashSet<String>();
            set5.add("");
            editor.putStringSet("featured body", set5);

            Set<String> set6= new HashSet<String>();
            set6.add("");
            editor.putStringSet("featured url", set6);

            editor.apply();
            return;
        }
        String titleAndBody = getWiki(entStr);
        String title = titleAndBody.substring(0, titleAndBody.indexOf("|"));
        String body = titleAndBody.substring(titleAndBody.indexOf("|") + 1, titleAndBody.length());



        Set<String> set= new HashSet<String>();
        set.add(title);
        editor.putStringSet("wikipedia title", set);

        Set<String> set1= new HashSet<String>();
        set1.add(body);
        editor.putStringSet("wikipedia body", set1);

        Set<String> set2= new HashSet<String>();
        set2.add(fetchedUrl);
        editor.putStringSet("wikipedia url", set2);

        editor.apply();







        //featured
        String entStr2 = "";
        URL url2;
        InputStream is2 = null;
        BufferedReader br2;
        String line2;
        String fetchedUrl2 = "";
        try
        {
            fetchedUrl2 = getFinalURL("https://en.wikipedia.org/wiki/Main_Page");
            url2 = new URL (fetchedUrl2);
            is2 = url2.openStream();  // throws an IOException
            br2 = new BufferedReader(new InputStreamReader(is2));
            while ((line2 = br2.readLine()) != null)
            {
                entStr2 = entStr2 + line2;
            }
        }
        catch (MalformedURLException mue)
        {
            mue.printStackTrace();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        finally
        {
            try
            {
                if (is2 != null) is2.close();
            }
            catch (IOException ioe)
            {
            }
        }

        entStr2 = entStr2.substring(0, entStr2.indexOf("article..."));
        entStr2 = entStr2.substring(entStr2.lastIndexOf("<a href="), entStr2.length());
        entStr2 = entStr2.substring(9, entStr2.indexOf("\"", 9));
        String featuredURL = "https://en.m.wikipedia.org" + entStr2;

        String entStr3 = "";
        URL url3;
        InputStream is3 = null;
        BufferedReader br3;
        String line3;
        String fetchedUrl3 = "";
        try
        {
            fetchedUrl3 = getFinalURL(featuredURL);
            System.out.println(fetchedUrl3);
            url3 = new URL (fetchedUrl3);
            is3 = url3.openStream();  // throws an IOException
            br3 = new BufferedReader(new InputStreamReader(is3));
            while ((line3 = br3.readLine()) != null)
            {
                entStr3 = entStr3 + line3;
            }
        }
        catch (MalformedURLException mue)
        {
            mue.printStackTrace();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        finally
        {
            try
            {
                if (is != null) is.close();
            }
            catch (IOException ioe)
            {
            }
        }

        String titleAndBody3 = getWiki(entStr3);
        String title3 = titleAndBody3.substring(0, titleAndBody3.indexOf("|"));
        String body3 = titleAndBody3.substring(titleAndBody3.indexOf("|") + 1, titleAndBody3.length());




        editor = mPrefs.edit();

        Set<String> set4= new HashSet<String>();
        set4.add(title3);
        editor.putStringSet("featured title", set4);

        Set<String> set5 = new HashSet<String>();
        set5.add(body3);
        editor.putStringSet("featured body", set5);

        Set<String> set6= new HashSet<String>();
        set6.add(fetchedUrl3);
        editor.putStringSet("featured url", set6);

        editor.apply();


    }

    public String getFinalURL(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setInstanceFollowRedirects(false);
        con.connect();
        con.getInputStream();

        if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
            String redirectUrl = con.getHeaderField("Location");
            return getFinalURL(redirectUrl);
        }
        return url;
    }
    public String getWiki(String entStr)
    {
        int a = entStr.indexOf("<title>");
        int b = entStr.indexOf(" - Wikipedia</title>");
        System.out.println("entStr" + entStr);
        String Title = entStr.substring(a+7, b+1);
        System.out.println(Title);

        String body = "";
        //first paragraphs always start after a <p>. first paragraphs always ends before a "mw-headline"
        int firstP = entStr.indexOf("<p>");
        int nextHeadline = entStr.indexOf("mw-headline");
        if (nextHeadline == -1)
        {
            nextHeadline = entStr.length();
        }
        body = entStr.substring(firstP, nextHeadline);
        //some articles have empty paragraphs. removes that paragraph so lastP isn't incorrect
        body = remove(body, "<p></p>");
        body = remove(body, "<p><br /></p>");

        //some articles have <p> in their tables
        //if (body.indexOf("</table") != -1) ///// previous version, slight chance it has better success rate
        if (body.lastIndexOf("</table") != -1)
        {
            int realFirstP = body.indexOf("<p>", body.lastIndexOf("</table"));
            //some articles have tables in the header (not the side ones). if statement keeps those cases from throwing out of bound error, since there will be no follwoing <p>'s
            if (realFirstP != -1)
            {
                body = body.substring(realFirstP, body.length());
            }
        }

        //finds the very last </p>
        int lastP = body.indexOf("</p>");
        while (body.indexOf("</p>", lastP + 1) != -1)
        {
            lastP = body.indexOf("</p>", lastP + 1);
        }
        body = body.substring(0, lastP);

        body = remove(body, "<p>");
        body = remove(body, "</p>");
        body = remove(body, "<b>");
        body = remove(body, "</b>");
        body = remove(body, "</a>");
        body = remove(body, "<i>");
        body = remove(body, "</i>");
        body = remove(body, "<br>");
        body = remove(body, "<br/>");
        body = remove(body, "<br />");
        body = remove(body, "<br/ >");
        body = remove(body, "&#160;");
        body = remove(body, "<small>");
        body = remove(body, "</small>");
        body = removeLayeredSection(body, "<span", "</span>");
        body = removeSection(body, "<a href", ">");
        body = removeSection(body, "<sup", "</sup>");
        body = removeSection(body, "<div", "</div>");
        body = removeSection(body, "<img", ">");

        System.out.println(body);
        String textAndBody = Title + "|" + body;
        return textAndBody;
    }


    public String remove(String body, String string)
    {
        int start = body.indexOf(string);
        while (start != -1)
        {
            body = body.substring(0, start) + body.substring(start + string.length(), body.length());
            start = body.indexOf(string);
        }
        return body;
    }
    public String removeSection(String body, String string1, String string2)
    {
        int start = body.indexOf(string1);
        int end = body.indexOf(string2, start + string1.length());
        while (start != -1 && end != -1)
        {
            body = body.substring(0, start) + body.substring(start + body.substring(start, end).length() + string2.length(), body.length());
            start = body.indexOf(string1);
            end = body.indexOf(string2, start + string1.length());
        }
        return body;
    }
    public String removeLayeredSection(String body, String string1, String string2)
    {
        int start = body.indexOf(string1);
        int end = body.indexOf(string2, start + string1.length());
        while (start != -1 && end != -1)
        {
            while (body.substring(start + 1, end).indexOf(string1) != -1)
            {
                start = body.indexOf(string1, start + 1);
            }

            body = body.substring(0, start) + body.substring(start + body.substring(start, end).length() + string2.length(), body.length());
            start = body.indexOf(string1);
            end = body.indexOf(string2, start + string1.length());
        }
        return body;
    }
}