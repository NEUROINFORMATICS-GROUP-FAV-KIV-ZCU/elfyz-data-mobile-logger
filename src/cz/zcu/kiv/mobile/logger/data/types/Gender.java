package cz.zcu.kiv.mobile.logger.data.types;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;


public enum Gender {
  MALE(Gender.LETTER_MALE, R.string.gender_male, 0),
  FEMALE(Gender.LETTER_FEMALE, R.string.gender_female, 1);
  
  public static final String LETTER_MALE = "M";
  public static final String LETTER_FEMALE = "F";
  
  private String letter;
  private int textId;
  private int index;
  
  
  private Gender(String letter, int textId, int index){
    this.letter = letter;
    this.textId = textId;
    this.index = index;
  }
  
  
  public String getLetter() {
    return letter;
  }
  
  public int getTextId() {
    return textId;
  }
  
  public int getIndex() {
    return index;
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
    return Application.getStringResource(textId);
  }
}
