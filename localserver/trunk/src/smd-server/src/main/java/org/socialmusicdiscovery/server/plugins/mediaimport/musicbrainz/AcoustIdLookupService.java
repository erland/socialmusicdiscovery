package org.socialmusicdiscovery.server.plugins.mediaimport.musicbrainz;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.plugins.mediaimport.squeezeboxserver.TrackListData;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class AcoustIdLookupService {

	@Inject
    @Named("musicbrainz.acoustid.servicekey")
	private String SERVICE_KEY;
	
	@Inject
    @Named("musicbrainz.acoustid.serviceurl")
	private String ACOUSTID_SERVICE_URL;
	
	public AcoustIdLookupService() {
		InjectHelper.injectMembers(this);
		if(this.ACOUSTID_SERVICE_URL == null) {
			throw new IllegalStateException("missing parameter 'musicbrainz.acoustid.serviceurl' please set acoustid service url un socialmusicdiscovery.properties or via -Dmusicbrainz.acoustid.serviceur");
		}
		if(this.SERVICE_KEY == null) {
			throw new IllegalStateException("missing parameter 'musicbrainz.acoustid.servicekey' please set acoustid service url un socialmusicdiscovery.properties or via -Dmusicbrainz.acoustid.servicekey");
		}
	}
	
	protected void lookup(int duration, String fingerprint) {
		Client client = Client.create();
		client.addFilter(new LoggingFilter(System.out));
		client.addFilter(new GZIPContentEncodingFilter(true));
		WebResource webResource = client.resource(this.ACOUSTID_SERVICE_URL);
		
// DONE: convert this (parameter in url) to form encoded post
//		JSONObject object = webResource.queryParam("client", SERVICE_KEY)
//					.queryParam("duration", "341")
//					.queryParam("meta", "recordings releasegroups")
//					.queryParam("fingerprint", "AQADtEokMsoUIv-hTxpCXbAOZ8fxBHh8ZJWyQG10IV8Jo2RyXMQf_B0udD08hFU0I7lyPCN6nDl-3IapIMc3pRCr7PCR4wnD48Plwyb64kdeVN2FT4d4HeHxiOiuVHB-_HCmwDvCF7pShFvho-SFC5fTodcMq_hx5sgsQbMf5GhJNMpH_Hj2Do-OUkeWMIfO0Mhz3ELzo4pzcK2OX8eVBz8lhNGoB-rR5kP4jCMaB9_64GkmEeeLKw2ao9YS5ExMIVl4SjgzEj5ROyiVwy_GLVkwRV0u_KmQh9CPXDnaHc6NJ1IIO9Th_uito0eWTLnwRDzUK0UY_UKv40sa4kq4o2GO6uiRp9B7ZIeffPiNypnwhKGKyaKw54fXw0h25MxJ_EKd6QmuJAXzoEQT5diVOcKppcJ1-A9-PJGGS8eT3XA2Bld2XCrCG5qeB00XPUIfDU-LhhG6LFmgZxTuZ2ge4vYhbolwUUrQJPPwrEV-5JeCB_0FLVMT_Eb04BOYS8K1RYe3GFVWNHGIWsh3JHuO4ymuB1b-oFZjfApqsYedVAipQ43GBqHEKNngXph-XJry4NGD-nBzCY-O5Bk75M7gSkfVXHikGObz4Y1E7AcT-UNJKceXyWiioz9KqqBqtMqS0LiKvHVENPHxR4O5bESt5KhzpDz6yMaR3ZBvRKSOJ8erBr_g487x9OjTR0iHKo2O-MtwSlliNOiPqwAacll09JmgSza8Rcc_SjgbCc6TC8nGIQyOLqTQ8D6eRhR-JOGRg4l19McTg09kODeeaAquG7mGhHnwl8E_4x-wJzzRK8J5oXnwDLEe_CnEPGFx4UQepUczGcluDrmI5tXgiDmcJOUxncih5UFILlsENkuOClOSJchFH8_SwE-xL8i-Dk8YVrgSdWheIbX2QfO2HXmi4kRDfqgSH6cUNXhvPGg69ESeBTehhw_6SHAkMYeuHI10_EGu6EWY9NCoJkPOWccfDU2oo0-PRyfOXDHOHEzc4jnSSUKyRtHRi8Mf1B-qZ0czZis46zgTPDqa_OjxPEGeF8my6ajx40eTL8JFwT9yQQyFfMGPw-qFlzh73PBI9Md_hNsWJLzQH9fR0_CMH_di5IMehHzx_PjRXCk6D6-OZ0d5vB9yFk0PnUP4ozluofhl4THuIX2gPUMuHUU7KegPH50sAP2RQzxyGJaJH-dx8_iRD8nIHHfR5kHj4EPz4GiqC2eC7Lag5Qj7pEJzGhWHh2GK9qrQtMVThOnW4IdYxwg7EtdRHS6FS1oMdzlO9AnaMxFxuQ7yHUkveMGPS3awJ1Jx6_BqPPORXLGQ3glRMbh2XDdKosmL44ktJHeqICSzY-OIUg44Bo-nUmgu9CjDo9kTDccjpOEryKmO7GiytUSf40d_g5Wy40uIo4leXDxG8viH8CfOIc9V5PKhM0d_Q5FyB77hNEFN4UsOvTpyhUcfM3A-oRZx5TiapZj2HElEhkcY_sGzJvgtw71xHgfTKGjSLchzJOdyME8UND94nK3w5cxRHo0YIvlyIg-q8dimtTiVHA2P-8TDCrX44sqLnMchHnsiVFbmQzeejBGFS8px9FoELTVOHmd2qGYQadePJk-HR8KPZ8KUHr2MH9mSy2hqKFJzhBZxBSfKLNfxo4njCR1DhPpyJEt0XLpQfdVhnElxwk9i_BFuHl2Y5fh0ofko1PhxMTbOHPZJXBnCcyG0PD0eJ8gXKWgXOAn-Gc2PP0WuH813lIT-Q22OiFty4Sfe5IFvYjqakcEVJUf4ZxA_5Dtq8fjxLB_OxUFFaWiOl8e1F60ueHfQ5zieoelRWg_MJ0ZyJj1y_ElaWEty4VyUYyJPpMs5iI-R5dmGUjp-PEJyZirCDNxbVOmCKc-EB9fy4AqS60f-CD8joUk-4zqm6qiVw_xxiulgEZfgHe2IPkMzHV9yhI0PJrOyCXcgdzH6HaF-nLnwDJPTE9cNHz1-DdFZPFGiQ-GiyBHCC1OeEc9wsNrCHC9x9Mnh58iZ6dCy7EH-4Z9gMcdToaUJJvtRj3iRIRmJCx96XbCOikPbGTeMXJahM8ilDz5KqsOP8oZlJegifch_6EEfFul19Ph39LiK8_jxR_iG9DuS45RyXDTg6Id3XEIO1UFOnHiLhsfR5fCJ50irQyeeI4-OHz7W48cRm4e2IZSO5lKML5nhB4-Y4sxxUUh9Do-PHxdEPp-Q33BZzNvwo4-O5kVeSDRCMcPjGM9wBc8Eo1QyCR-mH9EOjU-KeMN7TFcND8-NRlmIL8oRHgn_4j3646Jw1UHjHd0WxZhyJPuRRRuD5rOw7xGu40f1BP6O0vgxC40VHRXFHP0HxzuYnzhCSQmhR1kKKwh1-XiWgxcrPMkxiTquwEmk7ESPC2EelDzRp0YzEqqz4w_-o2qEJs6O_zgd7JmQJeJRkcd3HK6yYE9y_AgfQkvyI9-JinkOR8yM58F7-Ckq5kpw6Squ-ZiboMmPLNcF_Thy6fgT9EeTKLgNPkf9ID2Sjykc7sObY49PPMTJ4SlcG6HauEgcXfiPNtLRbMlxp0ZlimgUJh--4XNyNM5x4x_6IzqeZIEufQh7NL_AOduEfTryRB76w30hVoe5HLrQH13RPDo-5miOkMzxRhV-NE8kG8WXouFy1Dd-dHrwxziPT2nRjNlxSifyNSkci3iU0Icrznh23KhyNKFIAEGEAABQZIAAQhCIgDJESWGEFQgUAjwQhgDGmBACEIAZMAQpyAxFggAimABUSAKAMkQBBDAwAhCADAAIcCEAAIQoIAQBygFgiKGGIAMMAUIQQoAQQCBHgKBKCqOAMA4IBAgWBCnAGBQCWWeAdYApgQABRAAlEECYEGCcEYoJQYExgggkBKPUICEgMA44AABggAoAiRBAECaIMk5YIpQgCkEiHFGKIG0QAkwBQQABEgEFhAGEADCEYApDZIEkRgBojABIAWCEYAQRJ4iAVjDBARTAImEIEwYBoACRBAmgBBUKIQGAMpw4AwgBUCgAACOACYaIUEgZBBGiRhHhCFACMgQIQJAIRREBACBgAACCCUCAAQ4RAoCwhABjhDBCCQWUAMQAJAwiRhsEhqMAEAAIAQgAhIxTgAIAAFXGGSsgQwQwYQQABAmilCJGAKARAUApBFkChguEIADAMSCgUIYopgRxAAhgEAOCGAAQAkQoAIQQABDBiAJCIEIIEEIqACQRgiAGiCKEIAOgAIQECIgAQBhDmQLGKWEgIQAxAJxQAhBmlAMCAUIoQEQAKACQAhkEhTGGCCUgMIQAQYACRBjhnJCMECIEAg4JpaCiAgDgAQEACYKEUcoIgBiBBighAQCECEoAAsAqRYwDRhhDCAHIKMKUA1IAKoKChCAkgEEICCMEEc4IYgAARiDjBACACAEEBoAgZBQARAggIBFCAMJIZEJQIYRgyABGDHAUAAs0IMYQQ4QRAEMABERCOCAOgwwJgwAxwgjEBFDEKSKQoggJYIAUmAhjGACEEGGYEIQBgpCEQhjEgBDQACUQYAw4YgwFiCiBjAEIECAwQAAIBZAgQBoFgIEGC4SRIEABYAlzgBiCEKGAGUKFQBQB5Ag0gBgCGGIICGAclcoKQJyiABAIlAJAACKYsBA")
//					.accept(MediaType.APPLICATION_JSON)
//					.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
//					.post(JSONObject.class);
		
		// According to 
		// see https://blogs.oracle.com/enterprisetechtips/entry/consuming_restful_web_services_with
		// and http://stackoverflow.com/questions/8992857/using-the-jersey-client-to-do-a-post-operation-with-request-params-and-a-request
		// it seems that using a MultivaluedMap is mandatory for posting request as urlencoded form data

		MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
		formData.add("client", this.SERVICE_KEY);
		formData.add("duration", "341");
//		formData.add("meta", "recordings releasegroups compress");
//		formData.add("meta", "recordings recordingids releases releaseids releasegroups releasegroupids tracks puids usermeta sources");
		formData.add("meta", "recordings releases releasegroups sources tracks usermeta compress");
		formData.add("fingerprint", "AQADtEokMsoUIv-hTxpCXbAOZ8fxBHh8ZJWyQG10IV8Jo2RyXMQf_B0udD08hFU0I7lyPCN6nDl-3IapIMc3pRCr7PCR4wnD48Plwyb64kdeVN2FT4d4HeHxiOiuVHB-_HCmwDvCF7pShFvho-SFC5fTodcMq_hx5sgsQbMf5GhJNMpH_Hj2Do-OUkeWMIfO0Mhz3ELzo4pzcK2OX8eVBz8lhNGoB-rR5kP4jCMaB9_64GkmEeeLKw2ao9YS5ExMIVl4SjgzEj5ROyiVwy_GLVkwRV0u_KmQh9CPXDnaHc6NJ1IIO9Th_uito0eWTLnwRDzUK0UY_UKv40sa4kq4o2GO6uiRp9B7ZIeffPiNypnwhKGKyaKw54fXw0h25MxJ_EKd6QmuJAXzoEQT5diVOcKppcJ1-A9-PJGGS8eT3XA2Bld2XCrCG5qeB00XPUIfDU-LhhG6LFmgZxTuZ2ge4vYhbolwUUrQJPPwrEV-5JeCB_0FLVMT_Eb04BOYS8K1RYe3GFVWNHGIWsh3JHuO4ymuB1b-oFZjfApqsYedVAipQ43GBqHEKNngXph-XJry4NGD-nBzCY-O5Bk75M7gSkfVXHikGObz4Y1E7AcT-UNJKceXyWiioz9KqqBqtMqS0LiKvHVENPHxR4O5bESt5KhzpDz6yMaR3ZBvRKSOJ8erBr_g487x9OjTR0iHKo2O-MtwSlliNOiPqwAacll09JmgSza8Rcc_SjgbCc6TC8nGIQyOLqTQ8D6eRhR-JOGRg4l19McTg09kODeeaAquG7mGhHnwl8E_4x-wJzzRK8J5oXnwDLEe_CnEPGFx4UQepUczGcluDrmI5tXgiDmcJOUxncih5UFILlsENkuOClOSJchFH8_SwE-xL8i-Dk8YVrgSdWheIbX2QfO2HXmi4kRDfqgSH6cUNXhvPGg69ESeBTehhw_6SHAkMYeuHI10_EGu6EWY9NCoJkPOWccfDU2oo0-PRyfOXDHOHEzc4jnSSUKyRtHRi8Mf1B-qZ0czZis46zgTPDqa_OjxPEGeF8my6ajx40eTL8JFwT9yQQyFfMGPw-qFlzh73PBI9Md_hNsWJLzQH9fR0_CMH_di5IMehHzx_PjRXCk6D6-OZ0d5vB9yFk0PnUP4ozluofhl4THuIX2gPUMuHUU7KegPH50sAP2RQzxyGJaJH-dx8_iRD8nIHHfR5kHj4EPz4GiqC2eC7Lag5Qj7pEJzGhWHh2GK9qrQtMVThOnW4IdYxwg7EtdRHS6FS1oMdzlO9AnaMxFxuQ7yHUkveMGPS3awJ1Jx6_BqPPORXLGQ3glRMbh2XDdKosmL44ktJHeqICSzY-OIUg44Bo-nUmgu9CjDo9kTDccjpOEryKmO7GiytUSf40d_g5Wy40uIo4leXDxG8viH8CfOIc9V5PKhM0d_Q5FyB77hNEFN4UsOvTpyhUcfM3A-oRZx5TiapZj2HElEhkcY_sGzJvgtw71xHgfTKGjSLchzJOdyME8UND94nK3w5cxRHo0YIvlyIg-q8dimtTiVHA2P-8TDCrX44sqLnMchHnsiVFbmQzeejBGFS8px9FoELTVOHmd2qGYQadePJk-HR8KPZ8KUHr2MH9mSy2hqKFJzhBZxBSfKLNfxo4njCR1DhPpyJEt0XLpQfdVhnElxwk9i_BFuHl2Y5fh0ofko1PhxMTbOHPZJXBnCcyG0PD0eJ8gXKWgXOAn-Gc2PP0WuH813lIT-Q22OiFty4Sfe5IFvYjqakcEVJUf4ZxA_5Dtq8fjxLB_OxUFFaWiOl8e1F60ueHfQ5zieoelRWg_MJ0ZyJj1y_ElaWEty4VyUYyJPpMs5iI-R5dmGUjp-PEJyZirCDNxbVOmCKc-EB9fy4AqS60f-CD8joUk-4zqm6qiVw_xxiulgEZfgHe2IPkMzHV9yhI0PJrOyCXcgdzH6HaF-nLnwDJPTE9cNHz1-DdFZPFGiQ-GiyBHCC1OeEc9wsNrCHC9x9Mnh58iZ6dCy7EH-4Z9gMcdToaUJJvtRj3iRIRmJCx96XbCOikPbGTeMXJahM8ilDz5KqsOP8oZlJegifch_6EEfFul19Ph39LiK8_jxR_iG9DuS45RyXDTg6Id3XEIO1UFOnHiLhsfR5fCJ50irQyeeI4-OHz7W48cRm4e2IZSO5lKML5nhB4-Y4sxxUUh9Do-PHxdEPp-Q33BZzNvwo4-O5kVeSDRCMcPjGM9wBc8Eo1QyCR-mH9EOjU-KeMN7TFcND8-NRlmIL8oRHgn_4j3646Jw1UHjHd0WxZhyJPuRRRuD5rOw7xGu40f1BP6O0vgxC40VHRXFHP0HxzuYnzhCSQmhR1kKKwh1-XiWgxcrPMkxiTquwEmk7ESPC2EelDzRp0YzEqqz4w_-o2qEJs6O_zgd7JmQJeJRkcd3HK6yYE9y_AgfQkvyI9-JinkOR8yM58F7-Ckq5kpw6Squ-ZiboMmPLNcF_Thy6fgT9EeTKLgNPkf9ID2Sjykc7sObY49PPMTJ4SlcG6HauEgcXfiPNtLRbMlxp0ZlimgUJh--4XNyNM5x4x_6IzqeZIEufQh7NL_AOduEfTryRB76w30hVoe5HLrQH13RPDo-5miOkMzxRhV-NE8kG8WXouFy1Dd-dHrwxziPT2nRjNlxSifyNSkci3iU0Icrznh23KhyNKFIAEGEAABQZIAAQhCIgDJESWGEFQgUAjwQhgDGmBACEIAZMAQpyAxFggAimABUSAKAMkQBBDAwAhCADAAIcCEAAIQoIAQBygFgiKGGIAMMAUIQQoAQQCBHgKBKCqOAMA4IBAgWBCnAGBQCWWeAdYApgQABRAAlEECYEGCcEYoJQYExgggkBKPUICEgMA44AABggAoAiRBAECaIMk5YIpQgCkEiHFGKIG0QAkwBQQABEgEFhAGEADCEYApDZIEkRgBojABIAWCEYAQRJ4iAVjDBARTAImEIEwYBoACRBAmgBBUKIQGAMpw4AwgBUCgAACOACYaIUEgZBBGiRhHhCFACMgQIQJAIRREBACBgAACCCUCAAQ4RAoCwhABjhDBCCQWUAMQAJAwiRhsEhqMAEAAIAQgAhIxTgAIAAFXGGSsgQwQwYQQABAmilCJGAKARAUApBFkChguEIADAMSCgUIYopgRxAAhgEAOCGAAQAkQoAIQQABDBiAJCIEIIEEIqACQRgiAGiCKEIAOgAIQECIgAQBhDmQLGKWEgIQAxAJxQAhBmlAMCAUIoQEQAKACQAhkEhTGGCCUgMIQAQYACRBjhnJCMECIEAg4JpaCiAgDgAQEACYKEUcoIgBiBBighAQCECEoAAsAqRYwDRhhDCAHIKMKUA1IAKoKChCAkgEEICCMEEc4IYgAARiDjBACACAEEBoAgZBQARAggIBFCAMJIZEJQIYRgyABGDHAUAAs0IMYQQ4QRAEMABERCOCAOgwwJgwAxwgjEBFDEKSKQoggJYIAUmAhjGACEEGGYEIQBgpCEQhjEgBDQACUQYAw4YgwFiCiBjAEIECAwQAAIBZAgQBoFgIEGC4SRIEABYAlzgBiCEKGAGUKFQBQB5Ag0gBgCGGIICGAclcoKQJyiABAIlAJAACKYsBA");
		JSONObject response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(JSONObject.class, formData);
		System.out.println("answer: "+response.toString());
        ObjectMapper mapper = new ObjectMapper();
        List<AcoustIdWsResult> results = null;
        try {
			results  = mapper.readValue(response.getString("results"), new TypeReference<List<AcoustIdWsResult>>(){});
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(results.size());

// notes:
//		response.get("status")
//		JSONArray aze = new org.codehaus.jettison.json.JSONArray().
//		response.get("results").length()
//		((org.codehaus.jettison.json.JSONObject)response.get("results") instanceof org.codehaus.jettison.json.JSONArray
//		((org.codehaus.jettison.json.JSONArray)response.get("results")).get(0)  instanceof org.codehaus.jettison.json.JSONObject
//
//		((org.codehaus.jettison.json.JSONArray)response.get("results")).length()
		
	
	}
	
	public static void main(String [] args) {
		new AcoustIdLookupService().lookup(0,	"aa");
	}
}
