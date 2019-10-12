package com.liuchao.demo.sign;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import org.apache.commons.codec.binary.Hex;


import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 私匙加密 公匙解密
 * 参数的顺序必需是排序的，参数的位置不能变否则 验签不会通过
 */
public class Sign {

    public static String signWhole(String keycode, String param) {
        // 使用私钥加签
        byte[] signature = null;
        try {
            //获取privatekey
            byte[] keyByte = Base64.decode(keycode);
            KeyFactory keyfactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec encoderule = new PKCS8EncodedKeySpec(keyByte);
            PrivateKey privatekey = keyfactory.generatePrivate(encoderule);

            //用私钥给入参加签
            Signature sign = Signature.getInstance("SHA1WithRSA");
            sign.initSign(privatekey);
            sign.update(param.getBytes());

            signature = sign.sign();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }  catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SignatureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //将加签后的入参转成16进制

       // char[] chars = Hex.encodeHex(signature);
        String encode = Base64.encode(signature);

        return encode;
    }

    public static boolean verifyWhole(String param,String signature,String keycode){
        try {
            //获取公钥
            KeyFactory keyFactory=KeyFactory.getInstance("RSA");
            byte[] keyByte=Base64.decode(keycode);
            X509EncodedKeySpec encodeRule=new X509EncodedKeySpec(keyByte);
            PublicKey publicKey= keyFactory.generatePublic(encodeRule);

            //用获取到的公钥对   入参中未加签参数param 与  入参中的加签之后的参数signature 进行验签
            Signature sign=Signature.getInstance("SHA1WithRSA");
            sign.initVerify(publicKey);
            sign.update(param.getBytes());

            //将16进制码转成字符数组
            //byte[] hexByte=Hex.hexStringToBytes(signature);
            //byte[] bytes = signature.getBytes();
            byte[] decode = Base64.decode(signature);
            //验证签名
            return sign.verify(decode);

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SignatureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        // 商户私钥，您的PKCS8格式RSA2私钥
           String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC0AucnlnNId4AfIXKIagwqQmlm1KDLSPASPE0Mqm3Ajpv8crGVTePA1Op4bhMot+S6yPCzJ9ybg5350j9k1gCNN7D/7QKfTOM/JKwWgzQ/+8CpT7+qSo4lPzsx2n7eYPCOwMTYakGXyV73z0JrpudOTjblmG3wgLd/wSjA0co1EfsBFr0a9VzipjSDzpWOAwsW/h1KRl0aez8ESv9H644E4D+WaAuMFvlorlyFOSXDb4J82+RiBVBgyI86hiG6Nm2tfECQ8JVIv5J+2XkDFc/r691zuSsXxlm14uJXbtH6D3DcJ5VFp6n5hBd16TH9b2R/aBWk8IRcbMB1RKgM49K1AgMBAAECggEAbxoPk0Zvdr2SzSP2ra1OPtLofPglGfK1gXvITlXuEVezfLdJVY5SUJTY9mVx28LMIGCe9uRWa4FrgmI6kEDo7EAANaUR5kItS66Vq/tkx0zZl2ZKNI95dhG1tkpV3WjVcDYYCD5kp4s42xgHtau4vYLWrRRMXpEDMI2GWqPg+qlKUkhWu2W119TiExgLLkCTGOl7gBOQY3i2DRQjPLPUjygUi+q5F+ZCIVbrMZ4HG3iYu+5MnExv6XmYES3B9X1IPU3VL9SYHqUu+vhIC6Aap/FoRfs8aY3WB2Puap5pOdGIWfZagrGFS3FYFWF7drApOajYKSK3sTr7lEfwULqTbQKBgQDY/F/DiRdx+6srLR+Rrn/aOUOJtYNxe95Khw3qXZnkw+0lKjutV/Zy12k2Mr0NyupvgXTxSCoj5UfUH4sTzv6BQJJbigMNkopyMGrRf04/URfV/diANr9HGscvflmHJ6shRN2U7RS95RXXvsW9pG/ZW24Y6ADtp3Rsfh073IyvywKBgQDUYKHhYDg/0IB7rxxw6a9FFYVvmpCNNdYqXnQPMefISOAxtPEkO5Yv5rkwEL6UwXQUYJ1X9JqFqhFwZlp0ycMWHE6+U97QzApewNOVB0m7EmHAisKLpZ261aBA537MBqupmShxNcIUTpB91fnWFA2M0ADjD8OD0laksIt8aXw3fwKBgEdAF/INQjWbfklfN86Ojk0KILNHdzH8irHORzN+KNqFu4lIpO/2wmZTdajmYE33I6QZzlysZaoDz+JHuVN31Pp8SZLlbrs2M1/rpifEtdz0s+8Au+beE1E16dIemydWT0tNYbVVZEo2v0Voz2XGaTPmYfCrviJLql81ykOt2EShAoGAb3tyPn5lyInIiCR5x4PDr34Gl3kJXDbgf0kpnzhRNT+cADu9QYnN4sM4ftSrVibeqju1HJCX9IY7tUblQN0QfTDK3PnlwrD42Jhb+KTkvUB+87VjPi7nx0WQY8GhO7RctCBjrFnN0R8UlhmWlhvcqQgfDxIdTbtCJB1QezRcMj0CgYB7xcArTqc4xoO4+w7MigX4rl0uI3ZQWk2C2mCnxoEHCgveutoUIhpgslarrhYTMqer/+3gQJQ2k8VrKDOsBQd9fb43kWjbs7fSIAEeAzTSCtCc3+14p7bkP0EUSd7ECfwE+8Qqbo6+V1HBQiHdkHE6Y1SrTg4lNnHK/ZYnX4Euiw==";

        // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
          String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtALnJ5ZzSHeAHyFyiGoMKkJpZtSgy0jwEjxNDKptwI6b/HKxlU3jwNTqeG4TKLfkusjwsyfcm4Od+dI/ZNYAjTew/+0Cn0zjPySsFoM0P/vAqU+/qkqOJT87Mdp+3mDwjsDE2GpBl8le989Ca6bnTk425Zht8IC3f8EowNHKNRH7ARa9GvVc4qY0g86VjgMLFv4dSkZdGns/BEr/R+uOBOA/lmgLjBb5aK5chTklw2+CfNvkYgVQYMiPOoYhujZtrXxAkPCVSL+Sftl5AxXP6+vdc7krF8ZZteLiV27R+g9w3CeVRaep+YQXdekx/W9kf2gVpPCEXGzAdUSoDOPStQIDAQAB";
          String param="app_id=2014072300007148&biz_content={\"button\":[{\"actionParam\":\"ZFB_HFCZ\",\"actionType\":\"out\",\"name\":\"话费充值\"},{\"name\":\"查询\",\"subButton\":[{\"actionParam\":\"ZFB_YECX\",\"actionType\":\"out\",\"name\":\"余额查询\"},{\"actionParam\":\"ZFB_LLCX\",\"actionType\":\"out\",\"name\":\"流量查询\"},{\"actionParam\":\"ZFB_HFCX\",\"actionType\":\"out\",\"name\":\"话费查询\"}]},{\"actionParam\":\"http://m.alipay.com\",\"actionType\":\"link\",\"name\":\"最新优惠\"}]}&charset=GBK&method=alipay.mobile.public.menu.add&sign_type=RSA2&timestamp=2014-07-24 03:07:50&version=1.0";

        String s= signWhole(merchant_private_key, param);
        System.out.println(s);
        boolean b = verifyWhole(param, s, alipay_public_key);
        System.out.println(b);
    }

}
