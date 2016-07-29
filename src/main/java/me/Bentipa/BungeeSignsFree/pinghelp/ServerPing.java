package me.Bentipa.BungeeSignsFree.pinghelp;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServerPing {

    private boolean fetching;
    private InetSocketAddress host;
    private int timeout = 7000;
    private Gson gson = new Gson();

    public void setAddress(InetSocketAddress host) {
        this.host = host;
        this.fetching = false;
    }

    public InetSocketAddress getAddress() {
        return this.host;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public boolean isFetching() {
        return fetching;
    }

    public void setFetching(boolean pinging) {
        this.fetching = pinging;
    }

    public int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
            if ((k & 0x80) != 128) {
                break;
            }
        }
        return i;
    }

    public void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }

            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    @SuppressWarnings({"resource", "unused"})
    public SResponse fetchData() throws IOException {
        Socket socket = new Socket();
        OutputStream outputStream;
        DataOutputStream dataOutputStream;
        InputStream inputStream;
        InputStreamReader inputStreamReader;

        socket.setSoTimeout(this.timeout);
        socket.connect(host, timeout);

        outputStream = socket.getOutputStream();
        dataOutputStream = new DataOutputStream(outputStream);

        inputStream = socket.getInputStream();
        inputStreamReader = new InputStreamReader(inputStream);

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream handshake = new DataOutputStream(b);
        handshake.writeByte(0x00); //packet id for handshake
        writeVarInt(handshake, 4); //protocol version
        writeVarInt(handshake, this.host.getHostString().length()); //host length
        handshake.writeBytes(this.host.getHostString()); //host string
        handshake.writeShort(host.getPort()); //port
        writeVarInt(handshake, 1); //state (1 for handshake)

        writeVarInt(dataOutputStream, b.size()); //prepend size
        dataOutputStream.write(b.toByteArray()); //write handshake packet

        dataOutputStream.writeByte(0x01); //size is only 1
        dataOutputStream.writeByte(0x00); //packet id for ping
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        int size = readVarInt(dataInputStream); //size of packet
        int id = readVarInt(dataInputStream); //packet id

        if (id == -1) {
            throw new IOException("Premature end of stream.");
        }

        if (id != 0x00) { //we want a status response
            throw new IOException("Invalid packetID");
        }
        int length = readVarInt(dataInputStream); //length of json string

        if (length == -1) {
            throw new IOException("Premature end of stream.");
        }

        if (length == 0) {
            throw new IOException("Invalid string length.");
        }

        byte[] in = new byte[length];
        dataInputStream.readFully(in);  //read json string
        String json = new String(in);

        long now = System.currentTimeMillis();
        dataOutputStream.writeByte(0x09); //size of packet
        dataOutputStream.writeByte(0x01); //0x01 for ping
        dataOutputStream.writeLong(now); //time!?

        readVarInt(dataInputStream);
        id = readVarInt(dataInputStream);
        if (id == -1) {
            throw new IOException("Premature end of stream.");
        }

        if (id != 0x01) {
            throw new IOException("Invalid packetID");
        }
        long pingtime = dataInputStream.readLong(); //read response

        JSONObject jsono = new JSONObject();
        JSONParser parser = new JSONParser();
        try {
            jsono = (JSONObject) parser.parse(json);
        } catch (ParseException ex) {
            Logger.getLogger(ServerPing.class.getName()).log(Level.SEVERE, null, ex);
        }
        JSONObject version = (JSONObject) jsono.get("version");
        String vers = (String) version.get("name");

        SResponse ret = new SResponse();
        
        String cV = vers.substring(vers.length()-5, vers.length()-2);       
        if (cV.endsWith("1.9")) {            
            StatusResponse_19 res = gson.fromJson(json, StatusResponse_19.class);
            res.setTime((int) (now - pingtime));
            res.setTime((int) (now - pingtime));
            ret.setDescription(res.getDescription());
            ret.setFavicon(res.getFavicon());
            ret.setPlayers(res.getPlayers().getOnline());
            ret.setSlots(res.getPlayers().getMax());
            ret.setTime(res.getTime());
            ret.setProtocol(res.getVersion().getProtocol());
            ret.setVers(res.getVersion().getName());
        } else {            
            StatusResponse res = gson.fromJson(json, StatusResponse.class);
            res.setTime((int) (now - pingtime));
            ret.setDescription(res.getDescription());
            ret.setFavicon(res.getFavicon());
            ret.setPlayers(res.getPlayers().getOnline());
            ret.setSlots(res.getPlayers().getMax());
            ret.setTime(res.getTime());
            ret.setProtocol(res.getVersion().getProtocol());
            ret.setVers(res.getVersion().getName());
        }

        dataOutputStream.close();
        outputStream.close();
        inputStreamReader.close();
        inputStream.close();
        socket.close();

        return ret;
    }

    public class SResponse {

        private String vers;
        
        private String protocol;
        
        private String favicon;
        
        private int players;
        
        private int slots;
        
        private String description;
        
        private int time;

		public String getVers() {
			return vers;
		}

		public void setVers(String vers) {
			this.vers = vers;
		}

		public String getProtocol() {
			return protocol;
		}

		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}

		public String getFavicon() {
			return favicon;
		}

		public void setFavicon(String favicon) {
			this.favicon = favicon;
		}

		public int getPlayers() {
			return players;
		}

		public void setPlayers(int players) {
			this.players = players;
		}

		public int getSlots() {
			return slots;
		}

		public void setSlots(int slots) {
			this.slots = slots;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public int getTime() {
			return time;
		}

		public void setTime(int time) {
			this.time = time;
		}

    }

}
