package com.ibeiliao.deployment.cfg;


import com.ibeiliao.deployment.common.util.DES3;

/**
 * AES实现加解密
 * @author linyi
 *
 */
public class AesPropertiesEncoder implements PropertiesEncoder {
    
    /**
     * the key
     */
    public static final String KEY = "D5EFAEE2D37B423AAA43AC67B5EDC3FB";

    @Override
    public String encode(String str) {
        String s = "";
		try {
			s = DES3.encrypt(str, KEY);
		} catch (Exception e) {
			throw new RuntimeException("加密错误", e);
		}
        return s;
    }

    @Override
    public String decode(String str) {
        String s = "";
		try {
			s = DES3.decrypt(str, KEY);
		} catch (Exception e) {
			throw new RuntimeException("解密错误: " + str, e);
		}
        return s;
    }

    public static void main(String[] args) throws Exception {
        AesPropertiesEncoder encoder = new AesPropertiesEncoder();

        String encrypt = DES3.encrypt("root", AesPropertiesEncoder.KEY);
        System.out.println(encrypt);
        //System.out.println(DES3.decrypt(encrypt, AesPropertiesEncoder.KEY));
        String encrypt1 = DES3.encrypt("", AesPropertiesEncoder.KEY);
        System.out.println(encrypt1);
    }

}
