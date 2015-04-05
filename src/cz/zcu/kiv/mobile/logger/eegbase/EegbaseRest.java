package cz.zcu.kiv.mobile.logger.eegbase;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.AddExperimentDataResult;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.ExperimentParametersData;
import cz.zcu.kiv.mobile.logger.eegbase.data.login.UserInfo;
import cz.zcu.kiv.mobile.logger.eegbase.data.login.UserInfoWrapper;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.CommunicationException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.WrongCredentialsException;


public class EegbaseRest {
  
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
      
      ResponseEntity<UserInfoWrapper> response = restTemplate.exchange(getURL(), HttpMethod.GET, entity, UserInfoWrapper.class);
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
  
  //TODO predelat do real
  public static AddExperimentDataResult uploadGenericParameters(ExperimentParametersData parameters) throws CommunicationException, WrongCredentialsException {
    try {
      HttpHeaders requestHeaders = new HttpHeaders();
      requestHeaders.setAuthorization(new HttpBasicAuthentication("krupa@students.zcu.cz", "eegbase")); //TODO get from where?
      requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
      requestHeaders.setContentType(MediaType.APPLICATION_XML);
      
      RestTemplate restTemplate = new RestTemplate();
      restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());
      
      HttpEntity<ExperimentParametersData> entity = new HttpEntity<ExperimentParametersData>(parameters, requestHeaders);
      
      ResponseEntity<AddExperimentDataResult> response = restTemplate.exchange(URI.create("http://10.0.2.2:8080"), HttpMethod.POST, entity, AddExperimentDataResult.class);
      return response.getBody();
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
  }

  private static URI getURL() throws CommunicationException {
    String url = Application.getPreferences().getString("eegbase_url", null);
    try {
      return new URI(url);
    }
    catch (URISyntaxException e) {
      throw new CommunicationException("Unparsable EEGbase URL: " + url, e);
    }
  }
}
