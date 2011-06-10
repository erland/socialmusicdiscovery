package org.socialmusicdiscovery.server.plugins.mediaimport.lastfm;

import org.socialmusicdiscovery.server.business.service.browse.AbstractBrowseService;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AbstractLastFMBrowseService extends AbstractBrowseService {
    private static String ENCRYPTED_API_KEY = "B82AB1E88FD5B0CD86DCB29431628150F0E230C946292B2A5C2B4F58E8F29321549150E73EC69647";
    private static String KEY = "9E3254455757A464";

    protected String getLastFmUrl(String commandUrl) {
        return "http://ws.audioscrobbler.com/2.0/?api_key=" + getApiKey() + "&format=json&method=" + commandUrl;
    }

    private String getApiKey() {
        try {
            SecretKey key = new SecretKeySpec(getBytes(KEY), "DES");
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(getBytes(ENCRYPTED_API_KEY)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] getBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
