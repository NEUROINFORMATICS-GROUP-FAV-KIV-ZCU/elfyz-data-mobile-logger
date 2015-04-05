package cz.zcu.kiv.mobile.logger.layout;

/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

/**
 * This is a simple wrapper for {@link android.widget.LinearLayout} that implements the {@link android.widget.Checkable}
 * interface by keeping an internal 'checked' state flag.
 * <p>
 * This can be used as the root view for a custom list item layout for
 * {@link android.widget.AbsListView} elements with a
 * {@link android.widget.AbsListView#setChoiceMode(int) choiceMode} set.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
  private CheckedTextView checkBox;

  public CheckableLinearLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }


  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    checkBox = (CheckedTextView) findViewById(android.R.id.checkbox);
    if(checkBox == null)
      throw new IllegalStateException("Checkable linear layout does not contain checkbox with ID 'android.R.id.checkbox'");
  }

  @Override
  public boolean isChecked() {
    return checkBox.isChecked();
  }

  @Override
  public void setChecked(boolean checked) {
    checkBox.setChecked(checked);
  }

  @Override
  public void toggle() {
    checkBox.toggle();
  }
}
