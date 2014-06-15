import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class EasyHttp
{
	public static String getWithToken(String url)
	{
		String response = "";
		
		try
		{
			URL obj = new URL(String.format(url, Launcher.token));
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestProperty("Cookie",  ".ROBLOSECURITY=" + Launcher.accountCookie);
			con.setRequestMethod("GET");
			
			int responseCode = con.getResponseCode();
			
			if (responseCode == 420)
			{
				Launcher.token = URLEncoder.encode(con.getHeaderField("Token"), "UTF-8");
				obj = new URL(String.format(url, Launcher.token));
				con = (HttpURLConnection) obj.openConnection();
				con.setRequestProperty("Cookie", ".ROBLOSECURITY=" + Launcher.accountCookie);
				con.setRequestMethod("GET");
				con.connect();
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null)
			{
				response += inputLine;
			}
			
			in.close(); 
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return response;
	}
	
	public static String getNoToken(String url)
	{
		String response = "";
		
		try
		{
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestProperty("Cookie",  ".ROBLOSECURITY=" + Launcher.accountCookie);
			con.setRequestMethod("GET");
			
			int responseCode = con.getResponseCode();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null)
			{
				response += inputLine;
			}
			
			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return response;
	}
	
	public static String postWithToken(String url, String postData)
	{
		String response = "";
		
		try
		{
			URL obj = new URL(String.format(url, Launcher.token));
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestProperty("Cookie",  ".ROBLOSECURITY=" + Launcher.accountCookie);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			con.setRequestProperty("Content-Length", Integer.toString(postData.getBytes().length));
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postData);
			wr.flush();
			wr.close();
			
			int responseCode = con.getResponseCode();
			
			if (responseCode == 420)
			{
				Launcher.token = URLEncoder.encode(con.getHeaderField("Token"), "UTF-8");
				obj = new URL(String.format(url, Launcher.token));
				con = (HttpURLConnection) obj.openConnection();
				con.setRequestProperty("Cookie", ".ROBLOSECURITY=" + Launcher.accountCookie);
				con.setRequestMethod("POST");
				
				wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes(postData);
				wr.flush();
				wr.close();
				
				con.connect();
			}
			else if (responseCode == 200)
			{
				// TODO: Message successfully sent, maybe trigger something?
			}
			else
			{
				System.err.println(con.getResponseMessage());
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null)
			{
				response += inputLine;
			}
			
			in.close();
		}
		catch (Exception e)
		{
			Launcher.error(e.getMessage());
		}
		
		return response;
	}
	
	public static void login(String username, String password, int userId)
	{
		String postData = "username=" + username + "&password=" + password;
		
		try
		{
			URL obj = new URL(String.format(Links.GetUserInfo, userId));
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			int responseCode = con.getResponseCode();
			
			if (responseCode == 500)
				Launcher.error("Invalid user ID");
			else
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				String response = "";
				
				while ((inputLine = in.readLine()) != null)
				{
					response += inputLine;
				}
				
				in.close();
				UserInfo info = Launcher.gson.fromJson(response, UserInfo.class);
				
				if (info.Username.toLowerCase().equals(username.toLowerCase()))
				{
					HttpURLConnection.setFollowRedirects(false);
					obj = new URL(Links.Login);
					con = (HttpURLConnection) obj.openConnection();
					con.setRequestProperty("Host", "www.roblox.com");
					con.setRequestProperty("Connection", "keep-alive");
					con.setRequestProperty("Content-Length", Integer.toString(postData.getBytes().length));
					con.setRequestProperty("Cache-Control", "max-age=0");
					con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
					con.setRequestProperty("Origin", "http://www.roblox.com/");
					con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");
					con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					con.setRequestProperty("Referer", "http://www.roblox.com/Landing/Animated?logout=25190155");
					con.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
					con.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
					con.setRequestMethod("POST");
					con.setDoOutput(true);
					
					DataOutputStream wr = new DataOutputStream(con.getOutputStream());
					wr.writeBytes(postData);
					wr.flush();
					wr.close();
					
					con.connect();
					
					CookieManager msCookieManager = new java.net.CookieManager();
					
					Map<String, List<String>> headerFields = con.getHeaderFields();
					List<String> cookiesHeader = headerFields.get("Set-Cookie");
					
					if (cookiesHeader != null)
					{
						boolean foundLogin = false;
						
						for (String cookieStr : cookiesHeader) 
					    {
							List<HttpCookie> cookies = HttpCookie.parse(cookieStr);
							
							for (int i = 0; i < cookies.size(); i++)
							{
								HttpCookie cookie = cookies.get(i);
								
								if (cookie.getName().equals(".ROBLOSECURITY"))
								{
									foundLogin = true;
									System.out.println("Got login cookie!");
									Launcher.accountCookie = cookie.getValue();
								}
								else if (cookie.getName().equals("rbx-ForumSync"))
								{
									foundLogin = true;
									Launcher.userId = Integer.parseInt(cookie.getValue().substring(8));
								}
							}
					    }
						
						if (!foundLogin)
						{
							Launcher.error("Login failed");
						}
					}
					else
						Launcher.error("Too many login attempts. Please wait a while before trying again.");
					
					HttpURLConnection.setFollowRedirects(true);
				}
				else
					Launcher.error("User ID provided was for " + info.Username + ", not " + username);
			}
		}
		catch (Exception e)
		{
			Launcher.error(e.getMessage());
		}
	}
}