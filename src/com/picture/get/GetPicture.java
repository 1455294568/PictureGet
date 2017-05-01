package com.picture.get;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*; // implement File I/O and Stream I/O
import java.net.*; // implement HTTP connection
import org.jsoup.Jsoup; // HTML Parser tool
import java.util.ArrayList; //implement ArrayList<T>

import javax.imageio.ImageIO;

import org.jsoup.nodes.Element; //single element
import org.jsoup.nodes.Document; //place HTML source
import org.jsoup.select.Elements; //element collection

/**
 * This is a demo for Crawl a WEBsite
 * @author IAN
 *
 */
public class GetPicture {

	static ArrayList<String> alllinks = new ArrayList<String>();
	static boolean isexist = false;
	static String type="";
	static String temp,temp2;
	static int picnum=0; // Increase variable, implement changeable filename

	public static void main(String[] args) {
		
		
		File urlfile = new File("./imgurl.txt");
		if(urlfile.exists())
		{
			BufferedReader bufferedReader = null;
			System.out.println("Loading URLs from file...");
			try {
				bufferedReader = new BufferedReader(new FileReader(urlfile));
				String urltemp,urltempall = "";
				while ((urltemp = bufferedReader.readLine()) != null)
					urltempall += urltemp + "|";
					urltempall = urltempall.substring(0, urltempall.length()-1);
					
					for(String n :  urltempall.split("|"))
					{
						if(n.trim() != "")
							alllinks.add(n);
					}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try{
					if(bufferedReader != null)
						bufferedReader.close();
				}catch (Exception e) {
					
				}
			}
		}
		downloadurl("https://www.pexels.com/");
	}

	static void downloadurl(String _url) {
		try {
			Document doc = Jsoup.connect(_url).get();			
			Elements links = doc.select("a[href]");// link filter
			Elements picurls=doc.select("img");// Image filter
			//In this loop, it will get all the picture URL, and judge whether it's in the array or not
			//if not, download it, and add its URL to array
			for(Element picurl : picurls)
			{
				for(String i : alllinks)
				{
					if(i.equals(picurl.attr("src")))
					{
						isexist=true;
						break;
					}
					else
						isexist=false;
				}
				if(isexist==false)
				{
					alllinks.add(picurl.attr("src"));
					getpicurltofile(picurl.attr("src"));
					if(!picurl.attr("src").startsWith("http"))
						continue;
					else
						temp=picurl.attr("src");
					if(temp.contains("h=350"))
					{
						temp=temp.replace("h=350", "w=1920");
						downloadpic(temp);
					}
				}
				else
					continue;
			}
			//In this loop, it would get all the URl, and judge whether it's in the array or not
			//if not, add its URL to array and reuse this function to rescan this URL.
			for (Element link : links) 
			{
				for (String s : alllinks) {
					if (s.equals(link.attr("href"))) {
						isexist = true;
						break;
					} else
						isexist = false;
				}
				if (isexist == false) {
					if (link.attr("href").contains("http://") || link.attr("href").contains("https://")
							|| link.attr("href").contains("mailto") || link.attr("href").contains("png") || link.attr("href").contains("jpg") || link.attr("href").contains("jpeg"))
						continue;
					alllinks.add(link.attr("href"));
					getpicurltofile(link.attr("href"));
					temp2 = "https://www.pexels.com" + link.attr("href");
					System.out.println("Accessing " + temp2);
					downloadurl(temp2);
				} else
					continue;
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	//This function would download pictures from URL that SENDER serve, it would judge whether the URL contains "PNG" or "JPG" text
	//and give the file right extension name
	static void downloadpic(String _url)
	{
		HttpURLConnection connection=null;
		InputStream iStream=null;
		Image image=null;
		if(_url.contains("jpg"))
			type="jpg";
		else if(_url.contains("jpeg")) {
			type="jpeg";
		}
		else if (_url.contains("png")) {
			type="png";
		}
		else {
			return;
		}
		try
		{
			System.out.println("Downloading "+_url);
			URL url=new URL(_url);
			connection=(HttpURLConnection)url.openConnection();
			connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0" );
			iStream=connection.getInputStream();
			image=ImageIO.read(iStream);
			BufferedImage bufferedImage=(BufferedImage)image;
			File file=new File("./TaylorSwift/"+(picnum++)+"."+type);
			file.getParentFile().mkdirs();
			ImageIO.write(bufferedImage, type, file);
			iStream.close();
			connection.disconnect();
		}
		catch(Exception x)
		{
			x.printStackTrace();
		}
	}
	static void getpicurltofile(String _url)
	{
		
		BufferedWriter bufferedWriter=null;
		try
		{
			System.out.println("geting url:"+_url);
			File file=new File("./imgurl.txt");
			if(!file.exists())
				file.createNewFile();
			bufferedWriter=new BufferedWriter(new FileWriter(file, true));
			bufferedWriter.write(_url+"\n");
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(bufferedWriter!=null)
					bufferedWriter.close();
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}
	}
}