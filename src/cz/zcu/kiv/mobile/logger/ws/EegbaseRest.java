package cz.zcu.kiv.mobile.logger.ws;

import java.util.Collections;

import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import cz.zcu.kiv.mobile.logger.ws.data.UserInfo;
import cz.zcu.kiv.mobile.logger.ws.data.wrappers.UserInfoWrapper;
import cz.zcu.kiv.mobile.logger.ws.exceptions.CommunicationException;
import cz.zcu.kiv.mobile.logger.ws.exceptions.WrongCredentialsException;


public class EegbaseRest {
  public static final String WSURL = "http://eeg2.kiv.zcu.cz:8080/rest/user/login"; //TODO configurable

  
  public static UserInfo login(String email, String password) throws WrongCredentialsException, CommunicationException {
    UserInfo userinfo = null;
    try {
      HttpHeaders requestHeaders = new HttpHeaders();
      requestHeaders.setAuthorization(new HttpBasicAuthentication(email, password));
      requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      requestHeaders.setContentType(MediaType.APPLICATION_JSON);
      
      RestTemplate restTemplate = new RestTemplate();
      restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
      
      HttpEntity<Void> entity = new HttpEntity<Void>(requestHeaders);
      
      ResponseEntity<UserInfoWrapper> response = restTemplate.exchange(WSURL, HttpMethod.GET, entity, UserInfoWrapper.class);
      userinfo = response.getBody().getUserInfo();
    }
    catch (HttpClientErrorException e) {
      if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        throw new WrongCredentialsException(e);
      }
      else {
        throw new CommunicationException(e);
      }
    }
    catch (RestClientException e) {
      throw new CommunicationException(e);
    }
    return userinfo;
  }
}
