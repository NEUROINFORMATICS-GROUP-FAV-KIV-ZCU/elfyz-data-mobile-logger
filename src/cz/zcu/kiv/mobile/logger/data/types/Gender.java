package cz.zcu.kiv.mobile.logger.data.types;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;


public enum Gender {
  MALE(Gender.LETTER_MALE, R.string.gender_male),
  FEMALE(Gender.LETTER_FEMALE, R.string.gender_female);
  
  private static final String LETTER_MALE = "M";
  private static final String LETTER_FEMALE = "F";
  
  private String letter;
  private int textId;
  
  
  private Gender(String letter, int textId){
    this.letter = letter;
    this.textId = textId;
  }
  
  
  public String getLetter() {
    return letter;
  }
  
  public int getTextId() {
    return textId;
  }
  
  public static Gender fromLetter(String letter){
    if(LETTER_MALE.equalsIgnoreCase(letter))
      return MALE;
    if(LETTER_FEMALE.equalsIgnoreCase(letter))
      return FEMALE;
      
    throw new RuntimeException("Unsupported gender: letter=" + letter);
  }
  
  
  @Override
  public String toString() {
    return Application.getStringResource(textId); //TODO
  }
}
