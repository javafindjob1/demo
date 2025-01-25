package com.abc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 辅助小工具,完成人力之外的任务
 */
public class aaTest {

  public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {
    // String path = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\29AB8CD0C4271CA36A9C4411F267E81D\\29AB8CD0C4271CA36A9C4411F267E81D";
    // dir(path);
 
    String s = "if Yj==true then";
    System.out.println(s.matches("if \\w+([=]+)true then"));
    // 6083184329222009
    // 4457615268587626
    // 2115995444
    // 7383326457
    int bv = 116;
    String taskId1="6083184329222009";
    Random random = new Random();
    StringBuilder buf = new StringBuilder();
    buf.setLength(0);
    buf.append(random.nextInt(9)+1);
    for(int i=1; i<taskId1.length(); i++){
      buf.append(random.nextInt(10));
    }
    String ntaskId1 = buf.toString();
    System.out.println(ntaskId1);

    int requestId = 1;
    String sid = "6b49faa6f29944ce96c28d6823eb6906";

    buf.setLength(0);
    String arr = "abcdf01234";
    for(int i=0; i<sid.length(); i++){
      buf.append(arr.charAt(random.nextInt(10)));
    }
    String nsid = buf.toString();
    System.out.println(nsid);

    String cusorBody = "{\"routeKey\":\"300000000$dQICRFPiodUO\",\"token\":\"54ef5e36f0ddfe69f4642507aedef4cd\",\"sid\":\"6b49faa6f29944ce96c28d6823eb6906\",\"syncCommit\":false,\"requestId\":requestIdNew,\"head\":\"{\\\"type\\\":\\\"CURSOR_MESSAGE\\\",\\\"uid\\\":\\\"144115352407137277\\\",\\\"task_id\\\":taskIdNew,\\\"session_type\\\":0,\\\"base_rev\\\":baseRevNew,\\\"generalpacket\\\":{\\\"rel_rev\\\":\\\"hotfix-20241206_1724-0767bbbb\\\",\\\"dver\\\":\\\"3.0.28891287\\\",\\\"right_tag\\\":1},\\\"apid\\\":{\\\"s\\\":\\\"\\\",\\\"t\\\":timeNew,\\\"i\\\":\\\"DZFFJQ1JGUGlvZFVP\\\",\\\"g\\\":\\\"070063c428a6a1f37f58abaf70b90ca598fb29b3\\\",\\\"apid\\\":\\\"\\\",\\\"r\\\":114},\\\"docid\\\":\\\"300000000$dQICRFPiodUO\\\",\\\"code_ver\\\":0,\\\"gray\\\":0,\\\"dver\\\":\\\"3.0.0\\\",\\\"docs_type\\\":\\\"sheet\\\",\\\"wl\\\":\\\"\\\",\\\"cookie\\\":\\\"sheet_location={\\\\\\\"active_sheet\\\\\\\":\\\\\\\"BB08J2\\\\\\\",\\\\\\\"screen\\\\\\\":{\\\\\\\"w\\\\\\\":1920,\\\\\\\"h\\\\\\\":1080},\\\\\\\"dpr\\\\\\\":1,\\\\\\\"frozen\\\\\\\":{\\\\\\\"row\\\\\\\":-1,\\\\\\\"col\\\\\\\":-1},\\\\\\\"flow\\\\\\\":{\\\\\\\"row\\\\\\\":8,\\\\\\\"col\\\\\\\":0},\\\\\\\"scale\\\\\\\":1};fingerprint=a0b3aa264def46fa947d4e16000f080b47;low_login_enable=1;pgv_pvid=1676278908334762;RK=dC/9d/AjME;ptcz=c6604df7581ec082cd84a893f743fbf66c624f330e8b5f12fd66c39753a45340;wx_appid=wx02b8ff0031cec148;openid=oy6SixCdcgeZM9NNhDodN42t4n4s;fqm_pvqid=bf79b29a-a24a-4e15-b038-5a84e96054d3;uid=144115352407137277;uid_key=EOP1mMQHGixtTGFHenk4azZxbEFNcFNsdTkvMFZZeHRhbDBFODJwemQ2bTlCRFFObjhzPSKBAmV5SmhiR2NpT2lKQlEwTkJURWNpTENKMGVYQWlPaUpLVjFRaWZRLmV5SlVhVzU1U1VRaU9pSXhORFF4TVRVek5USTBNRGN4TXpjeU56Y2lMQ0pXWlhJaU9pSXhJaXdpUkc5dFlXbHVJam9pYzJGaGMxOTBiMk1pTENKU1ppSTZJbEJ1VjNsUmRpSXNJbVY0Y0NJNk1UY3pORFE1TmpnME9Dd2lhV0YwSWpveE56TXhPVEEwT0RRNExDSnBjM01pT2lKVVpXNWpaVzUwSUVSdlkzTWlmUS5EOWpXZWVUSnNNUWxPVHNEeHFYU3AtTG9ZMWlBZ1hlYm1ReEJ4Y1VFemhzKNCkibsG;utype=wx;access_token=86_-RYBfCPNoOhy0EKMBz-WCjdOEjXR6S_ZJwe2lyU6fPVDUvqAXjTZANAwBK-uCnjHO9RGkD3Js3bWzIqxXwOu0HsHilX-BsWTEcT0jXdN60E;refresh_token=86_4yqhHJqwUWG5BR_4bE8fxBmQMklslgzLb3igDWXVQ4hSqKAyc0Ijph9umN72zu8-iYiBzNQxJnWv7XDGTuCSMh9IacYUZPB6K-5rJgZdY-U;gray_user=true;DOC_SID=6864d1e91ba1479bad0654bf9a161ee5624c46e584634742a46717dd460b3611;SID=6864d1e91ba1479bad0654bf9a161ee5624c46e584634742a46717dd460b3611;adtag=s_qq_grpfile_top;adtag=s_qq_grpfile_top;loginTime=1732823460436;polish_tooltip=true;optimal_cdn_domain=docs2.gtimg.com;backup_cdn_domain=docs.gtimg.com;docMessageCenterCookie=;co_fm_tx_doc=7Qrc441UoY6TxY9S;eas_sid=q1G7e3v3g4U2Z9w7p5p7A5O8i3;_qimei_uuid42=18c06070b3a100cce1bf41e3556fd5b16bbe687a2b;_qimei_fingerprint=12fc697b32f9e8a374c3b5ce92673b58;_qimei_h38=8f954fbae1bf41e3556fd5b10200000b818c06;uin_cookie=o767690811;ied_qq=o767690811;qq_domain_video_guid_verify=5da2edd3be0b882e;_qimei_q32=8297291f91d4eedb7b11e53041c00477;_qimei_q36=2c12241694087f62f9e2f06330001c41811d;o_cookie=767690811;traceid=95cd4e1398;TOK=95cd4e1398bd63bc;hashkey=95cd4e13\\\"}\",\"body\":\"{\\\"type\\\":\\\"caret\\\",\\\"sheet\\\":{\\\"coords\\\":[18,5,18,5],\\\"isEditing\\\":true,\\\"tabId\\\":\\\"BB08J2\\\",\\\"openTime\\\":1733556496471},\\\"changedBy\\\":\\\"p.144115352407137277\\\"}\"}";
    cusorBody = cusorBody.replace("requestIdNew", requestId+"");
    cusorBody = cusorBody.replace("taskIdNew", ntaskId1);
    cusorBody = cusorBody.replace("baseRevNew", bv+"");
    cusorBody = cusorBody.replace("timeNew", new Date().getTime()+"");
    System.out.println("cusorBody");
    System.out.println(cusorBody);

    String taskId2 = "2115995444";
    buf.setLength(0);
    buf.append(random.nextInt(9)+1);
    for(int i=1; i<taskId2.length(); i++){
      System.out.print(random.nextInt(10));
    }
    String ntaskId2 = buf.toString();
    System.out.println(ntaskId2);
   
    String changeBody = "{\"routeKey\":\"300000000$dQICRFPiodUO\",\"token\":\"54ef5e36f0ddfe69f4642507aedef4cd\",\"sid\":\"6b49faa6f29944ce96c28d6823eb6906\",\"syncCommit\":false,\"requestId\":56,\"head\":\"{\\\"type\\\":\\\"USER_CHANGES\\\",\\\"uid\\\":\\\"144115352407137277\\\",\\\"task_id\\\":1664307776,\\\"session_type\\\":0,\\\"base_rev\\\":115,\\\"generalpacket\\\":{\\\"rel_rev\\\":\\\"hotfix-20241206_1724-0767bbbb\\\",\\\"dver\\\":\\\"3.0.28891287\\\",\\\"right_tag\\\":1},\\\"apid\\\":{\\\"s\\\":\\\"886577919402\\\",\\\"t\\\":1733537591861,\\\"i\\\":\\\"DZFFJQ1JGUGlvZFVP\\\",\\\"g\\\":\\\"070063c428a6a1f37f58abaf70b90ca598fb29b3\\\",\\\"apid\\\":\\\"\\\",\\\"r\\\":114},\\\"docid\\\":\\\"300000000$dQICRFPiodUO\\\",\\\"code_ver\\\":0,\\\"gray\\\":0,\\\"dver\\\":\\\"3.0.0\\\",\\\"docs_type\\\":\\\"sheet\\\",\\\"wl\\\":\\\"\\\",\\\"cookie\\\":\\\"sheet_location={\\\\\\\"active_sheet\\\\\\\":\\\\\\\"BB08J2\\\\\\\",\\\\\\\"screen\\\\\\\":{\\\\\\\"w\\\\\\\":1920,\\\\\\\"h\\\\\\\":1080},\\\\\\\"dpr\\\\\\\":1,\\\\\\\"frozen\\\\\\\":{\\\\\\\"row\\\\\\\":-1,\\\\\\\"col\\\\\\\":-1},\\\\\\\"flow\\\\\\\":{\\\\\\\"row\\\\\\\":8,\\\\\\\"col\\\\\\\":0},\\\\\\\"scale\\\\\\\":1};fingerprint=a0b3aa264def46fa947d4e16000f080b47;low_login_enable=1;pgv_pvid=1676278908334762;RK=dC/9d/AjME;ptcz=c6604df7581ec082cd84a893f743fbf66c624f330e8b5f12fd66c39753a45340;wx_appid=wx02b8ff0031cec148;openid=oy6SixCdcgeZM9NNhDodN42t4n4s;fqm_pvqid=bf79b29a-a24a-4e15-b038-5a84e96054d3;uid=144115352407137277;uid_key=EOP1mMQHGixtTGFHenk4azZxbEFNcFNsdTkvMFZZeHRhbDBFODJwemQ2bTlCRFFObjhzPSKBAmV5SmhiR2NpT2lKQlEwTkJURWNpTENKMGVYQWlPaUpLVjFRaWZRLmV5SlVhVzU1U1VRaU9pSXhORFF4TVRVek5USTBNRGN4TXpjeU56Y2lMQ0pXWlhJaU9pSXhJaXdpUkc5dFlXbHVJam9pYzJGaGMxOTBiMk1pTENKU1ppSTZJbEJ1VjNsUmRpSXNJbVY0Y0NJNk1UY3pORFE1TmpnME9Dd2lhV0YwSWpveE56TXhPVEEwT0RRNExDSnBjM01pT2lKVVpXNWpaVzUwSUVSdlkzTWlmUS5EOWpXZWVUSnNNUWxPVHNEeHFYU3AtTG9ZMWlBZ1hlYm1ReEJ4Y1VFemhzKNCkibsG;utype=wx;access_token=86_-RYBfCPNoOhy0EKMBz-WCjdOEjXR6S_ZJwe2lyU6fPVDUvqAXjTZANAwBK-uCnjHO9RGkD3Js3bWzIqxXwOu0HsHilX-BsWTEcT0jXdN60E;refresh_token=86_4yqhHJqwUWG5BR_4bE8fxBmQMklslgzLb3igDWXVQ4hSqKAyc0Ijph9umN72zu8-iYiBzNQxJnWv7XDGTuCSMh9IacYUZPB6K-5rJgZdY-U;gray_user=true;DOC_SID=6864d1e91ba1479bad0654bf9a161ee5624c46e584634742a46717dd460b3611;SID=6864d1e91ba1479bad0654bf9a161ee5624c46e584634742a46717dd460b3611;adtag=s_qq_grpfile_top;adtag=s_qq_grpfile_top;loginTime=1732823460436;polish_tooltip=true;optimal_cdn_domain=docs2.gtimg.com;backup_cdn_domain=docs.gtimg.com;docMessageCenterCookie=;co_fm_tx_doc=7Qrc441UoY6TxY9S;eas_sid=q1G7e3v3g4U2Z9w7p5p7A5O8i3;_qimei_uuid42=18c06070b3a100cce1bf41e3556fd5b16bbe687a2b;_qimei_fingerprint=12fc697b32f9e8a374c3b5ce92673b58;_qimei_h38=8f954fbae1bf41e3556fd5b10200000b818c06;uin_cookie=o767690811;ied_qq=o767690811;qq_domain_video_guid_verify=5da2edd3be0b882e;_qimei_q32=8297291f91d4eedb7b11e53041c00477;_qimei_q36=2c12241694087f62f9e2f06330001c41811d;o_cookie=767690811;traceid=95cd4e1398;TOK=95cd4e1398bd63bc;hashkey=95cd4e13;clean_env=0\\\"}\",\"body\":\"{\\\"baseRev\\\":115,\\\"changeSetCount\\\":1,\\\"changeset\\\":\\\"eF7jsuBgFWDQMuIQnMSoKyXAxebkZGDhZSQgJMGqIKTBqsSgxcvFzcX5fPaWZ5PWPtmx1oiDg0WIiYMhghEAF+sLtQ==\\\",\\\"apool\\\":\\\"\\\",\\\"keydata\\\":false,\\\"head\\\":false,\\\"subId\\\":\\\"BB08J2\\\",\\\"oSubId\\\":false,\\\"taskId\\\":1664307776,\\\"offlineMsg\\\":false,\\\"type\\\":\\\"USER_CHANGES\\\",\\\"uuId\\\":\\\"p.144115352407137277\\\",\\\"pver\\\":0}\"}";
    changeBody = changeBody.replace("\\", "");

    System.out.println("changeBody");
    System.out.println(changeBody);
    
    long time = new Date().getTime();
    System.out.println(time);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String format = simpleDateFormat.format(new Date(time));
    System.out.println(format);
    testout();
    // ChannelHandlerContext
    // ChunkedWriteHandler
  }

  /**
   * 递归查找war3map.j文件 分析j被隐藏的问题
   * @param path
   * @throws UnsupportedEncodingException
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static void dir(String path) throws UnsupportedEncodingException, FileNotFoundException, IOException {
    if (new File(path).isFile()) {
      try (BufferedReader br = new BufferedReader(
          new InputStreamReader(new FileInputStream(path), "utf8"))) {
        String line = null;
        while ((line = br.readLine()) != null) {
          if (line.contains("function")) {
            System.out.println(path);
            break;
          }
        }
      }
    } else {
      String[] list = new File(path).list();
      for (String s : list) {
        dir(path + "\\" + s);
      }
    }
  }

  /**
   * 格式化test, 将test 中的换行替换为|n 输出testout, 方便在mark.ini中使用
   * @throws UnsupportedEncodingException
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static void testout() throws UnsupportedEncodingException, FileNotFoundException, IOException {

    List<String> list = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(aaTest.class.getResourceAsStream("custom/test"), "utf-8"))) {
      String line = null;
      Pattern pat = Pattern.compile("^\\w{4}\\s");
      String preLine = "";
      while ((line = br.readLine()) != null) {
        Matcher matcher = pat.matcher(line);
        if (matcher.find()) {
          // 新行
          list.add(preLine);
          preLine = line;
        } else {
          preLine += "|n" + line;
        }
      }
      list.add(preLine);

    } catch (IOException e) {
      e.printStackTrace();
    }
    try (BufferedWriter bw = new BufferedWriter(
        new OutputStreamWriter(
            new FileOutputStream("C:\\Users\\76769\\Desktop\\demo\\demo\\src\\main\\java\\com\\abc\\custom\\testout"),
            "utf-8"))) {
      for (String s : list) {
        bw.write(s);
        bw.newLine();
      }
    }

  }
}
