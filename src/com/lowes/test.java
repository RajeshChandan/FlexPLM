package com.lowes;

public class test {

    public static void main(String[] args) {

        System.out.println("dfbvrgntr");
        String version = "A.4";
        System.out.println(version);
        String regex = "\\.";
        System.out.println(version.contains(regex));
        System.out.println(version.split(regex)[0]);
        System.out.println(version.split(regex)[1]);
        version = version.split(regex)[0] + "." + (Integer.valueOf(version.split(regex)[1]) + 1);
        System.out.println(version);
      /*  System.out.println("wferervr");
        String json = "{\"className\":\"com.lcs.wc.document.LCSDocument\",\"notificationType\":\"DocumentPublished\",\"flexType\":\"Published\",\"validationType\":\"or\",\"criteria\":[{\"criteriaType\":\"TRIGGERED_OBJECT\",\"criteriaSubType\":\"ANY_CHANGE\",\"criteriaValue\":\"lwsStatus==lwsActive\"},{\"criteriaType\":\"TRIGGERED_OBJECT\",\"criteriaSubType\":\"ANY_CHANGE\",\"criteriaValue\":\"lwsPerfDocType==lwsPublished\"}],\"roles\":\"ASCS Product Manager\",\"recipientType\":\"SYSTEM_ROLEUSER\",\"subscriber\":\"rajeshchandan.sahu@lowes.com,vc.jeyaganeshan.ramachandran@lowes.com\"}";
        boolean s = new TriggerConfigUtil().updateConfig(json);

        System.out.println(s);
*/
       /* JSONParser var2 = new JSONParser();
        try {
            FileReader var7 = new FileReader("/C:\\Users\\4764479\\OneDrive - Lowe's Companies Inc\\@WORK\\Lowes\\VS_WRKSPACE\\Lowes Notifications\\notificationTriggerConfig.json");
            JSONObject json = (JSONObject) var2.parse(var7);
            NotificationConfigs jsonPayload = new ObjectMapper().readValue(json.toJSONString(), NotificationConfigs.class);

            System.out.println(jsonPayload.getSendNotification());

            ActionTrigger
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }*/


    }

}
