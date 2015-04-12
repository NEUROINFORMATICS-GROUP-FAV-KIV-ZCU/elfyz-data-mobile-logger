package cz.zcu.kiv.mobile.logger.eegbase;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

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
import cz.zcu.kiv.mobile.logger.eegbase.data.get_experiment_list.ExperimentList;
import cz.zcu.kiv.mobile.logger.eegbase.data.login.UserInfo;
import cz.zcu.kiv.mobile.logger.eegbase.data.login.UserInfoWrapper;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.CommunicationException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.WrongCredentialsException;


public class EegbaseRest {
  private static final String ENDPOINT_USER_LOGIN = "/user/login";
  private static final String ENDPOINT_MY_EXPERIMENT_LIST = "/experiment/myList"; //TODO endpoints
  private static final String ENDPOINT_UPLOAD_GENERIC_PARAMETERS = "/experiment/parameters";
  
  
  public static UserInfo login(String email, String password) throws WrongCredentialsException, CommunicationException {
    UserInfo userinfo = null;
    try {
      HttpHeaders requestHeaders = new HttpHeaders();
      requestHeaders.setAuthorization(new HttpBasicAuthentication(email, password));
      requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML));
      requestHeaders.setContentType(MediaType.APPLICATION_JSON);
      
      RestTemplate restTemplate = new RestTemplate();
      restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
      restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());
      
      HttpEntity<Void> entity = new HttpEntity<Void>(requestHeaders);
      
      ResponseEntity<UserInfoWrapper> response = restTemplate.exchange(getURI(ENDPOINT_USER_LOGIN), HttpMethod.GET, entity, UserInfoWrapper.class);
      userinfo = response.getBody().getUserInfo();
      return userinfo;
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
  
  public static ExperimentList getMyExperiments(String email, String password) throws WrongCredentialsException, CommunicationException {
    ExperimentList experimentList = null;
    try {
      HttpHeaders requestHeaders = new HttpHeaders();
      requestHeaders.setAuthorization(new HttpBasicAuthentication(email, password));
      requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML));
      requestHeaders.setContentType(MediaType.APPLICATION_JSON);
      
      RestTemplate restTemplate = new RestTemplate();
      restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
      restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());
      
      HttpEntity<Void> entity = new HttpEntity<Void>(requestHeaders);
      
      ResponseEntity<ExperimentList> response = restTemplate.exchange(getURI(ENDPOINT_MY_EXPERIMENT_LIST), HttpMethod.GET, entity, ExperimentList.class);
      experimentList = response.getBody();
      return experimentList;
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
    catch (Exception e) {
      throw new CommunicationException("Serious communication error.", e);
    }
  }
  
  public static AddExperimentDataResult uploadGenericParameters(String email, String password, ExperimentParametersData parameters) throws CommunicationException, WrongCredentialsException {
    try {
      HttpHeaders requestHeaders = new HttpHeaders();
      requestHeaders.setAuthorization(new HttpBasicAuthentication(email, password));
      requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML));
      requestHeaders.setContentType(MediaType.APPLICATION_XML);
      
      RestTemplate restTemplate = new RestTemplate();
      restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
      restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());
      
      HttpEntity<ExperimentParametersData> entity = new HttpEntity<ExperimentParametersData>(parameters, requestHeaders);
      
      ResponseEntity<AddExperimentDataResult> response = restTemplate.exchange(getURI(ENDPOINT_UPLOAD_GENERIC_PARAMETERS), HttpMethod.POST, entity, AddExperimentDataResult.class);
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

  private static URI getURI(String endpoint) throws CommunicationException {
    String uri = Application.getPreferences().getString("eegbase_url", null) + endpoint;
    try {
      return new URI(uri);
    }
    catch (URISyntaxException e) {
      throw new CommunicationException("Unparsable EEGbase URI: " + uri, e);
    }
  }
}
