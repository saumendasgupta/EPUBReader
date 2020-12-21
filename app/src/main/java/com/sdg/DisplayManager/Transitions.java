
package com.sdg.DisplayManager;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.android.QuikE.R;
import com.sdg.organizer.Organizer;

public class Transitions {
	/** Called when the activity is first created. */

	public Transitions(Context ctx) {
		// TODO Auto-generated constructor stub
	}

	// This method is for transition of page on pressing Next
	public void pageNextTransition(Display_Manager display_Manager,
			RelativeLayout mRelativeLayout) {
		LayoutAnimationController controller = AnimationUtils
				.loadLayoutAnimation(display_Manager, R.anim.pagelayout_next_in);

		mRelativeLayout.setLayoutAnimation(controller);

	}// end of method pageNextTransition()

	// This method is for transition of page on pressing Previous
	public void pagePreviousTransition(Display_Manager display_Manager,
			RelativeLayout mRelativeLayout) {
		LayoutAnimationController controller = AnimationUtils
				.loadLayoutAnimation(display_Manager, R.anim.pagelayout_prev_in);
		mRelativeLayout.setLayoutAnimation(controller);
		
	}// end of method pagePreviousTransition()

	// This method is for showing the animation / transition effect when the
	// book is open from MyLibrary
	public void bookOpenInGrid(TextView mTextToTransit,
			ImageView mImageToTransit, AbsoluteLayout mTransitionContainer,
			View v) {

		// for positioning of the image and text
		int mBookIconTop = v.getTop();
		int mBookIconLeft = v.getLeft() + 15;
		int mBookTextLeft = v.getLeft();
		int mBookTextTop = v.getTop() + 50;

		// for defining animation parameters
		int fromXDelta = 0;
		int fromYDelta = 0;
		int toYDelta = 175 - v.getTop();
		int toXDelta = 165 - v.getLeft();

		// Setting the parameters for image and text views from where the
		// animation is to be started
		mImageToTransit.setLayoutParams(new AbsoluteLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				mBookIconLeft, mBookIconTop));
		mTextToTransit.setLayoutParams(new AbsoluteLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				mBookTextLeft, mBookTextTop));

		// adding the image and text view to the absolute layout
		mTransitionContainer.addView(mImageToTransit);
		mTransitionContainer.addView(mTextToTransit);

		// Defining the animation for views
		Animation an = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta,
				toYDelta);
		Animation an1 = new ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f, 0.5f, 0.5f);
		

		// Defining interpolator for animation
		DecelerateInterpolator i = new DecelerateInterpolator();
		an.setInterpolator(i);

		AnimationSet set = new AnimationSet(true);
		set.addAnimation(an);
		set.addAnimation(an1);
		
		
		// setting the naimtion duration
		set.setDuration(1500);

		// Applying the animation / transition
		mImageToTransit.setAnimation(set);
		mTextToTransit.setAnimation(an);

	}//end of method bookOpenInGrid()

	@SuppressWarnings("deprecation")
	// This method defines all the animation to be done for animating the
	// categories or showing the translation
	public void categoryTransition(int position,
			AbsoluteLayout mCateTransitionContainer, Organizer organizer) {

		// setting the animation duration and repeat count
		int mCateAnimDuration = 350;
		int mCateAnimRepeatCount = 1;

		// setting the animation parameters
		int fromXDelta = 0;
		int fromYDelta = 0;
		int toXDelta = 95;
		int toYDelta = 10;

		// declaring the view parameters of the icons/ImageView that are added
		// to absolute layout for showing the animation/ transition
		int mCateIconWidth = 0;
		int mCateIconHeight = 0;
		int mCateIconLeft = 0;
		int mCateIconTop = 0;

		// Creating the ImageViews to be added on absolute layout for showing
		// the transition/ animation
		ImageView[] mCateImage = new ImageView[6];

		// This for loop defines the logic for setting the
		// parameters(x,y,width,height) of the image view/note category icons to
		// be added to absolute layout for animation and placing them on it
		for (int i = 0; i < 6; i++)

		{
			mCateImage[i] = new ImageView(organizer);

			// setting the icon for note categories
			if (i == 2)
				mCateImage[i].setBackgroundResource(R.drawable.allnotes);
			else
				mCateImage[i].setBackgroundResource(R.drawable.miscnotes);

			// setting the view parameters of ImageViews for their placement on
			// absolute layout
			if (i == 0) {
				mCateIconLeft = 29;
				mCateIconTop = 57;
				mCateIconWidth = LayoutParams.WRAP_CONTENT;
				mCateIconHeight = LayoutParams.WRAP_CONTENT;
			} else if (i == 1) {
				mCateIconWidth = 50;
				mCateIconHeight = 50;
				mCateIconLeft = 124;
				mCateIconTop = 45;
			} else if (i == 2) {

				mCateIconWidth = 54;
				mCateIconHeight = 60;
				mCateIconLeft = 218;
				mCateIconTop = 25;
			} else if (i == 3) {
				mCateIconWidth = 50;
				mCateIconHeight = 50;
				mCateIconLeft = 316;
				mCateIconTop = 45;
			} else if (i == 4) {
				mCateIconLeft = 412;
				mCateIconTop = 57;
				mCateIconWidth = LayoutParams.WRAP_CONTENT;
				mCateIconHeight = LayoutParams.WRAP_CONTENT;
			}

			// Setting the layout parameters of ImageView on AbsoluteLayout
			mCateImage[i].setLayoutParams(new AbsoluteLayout.LayoutParams(
					mCateIconWidth, mCateIconHeight, mCateIconLeft,
					mCateIconTop));

			// adding all ImageViews to the AbsoluteLayout
			mCateTransitionContainer.addView(mCateImage[i]);
		}

		// this is for setting the view parameters of an extra imageview that is
		// to be shown coming from out of the screen either from left or right
		// based on the position clicked
		if (position < 2) {
			mCateImage[5].setLayoutParams(new AbsoluteLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, -70,
					70));
		} else if (position > 2)
			mCateImage[5].setLayoutParams(new AbsoluteLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 504,
					70));

		// Animation for ImageView moving up and right (ImageView 0, 1, 5)
		Animation mAnimRightUp = new TranslateAnimation(fromXDelta, toXDelta,
				fromYDelta, -toYDelta);
		mAnimRightUp.setDuration(mCateAnimDuration);

		// Animation for ImageView moving down and right (ImageView 3 and 4)
		Animation mAnimRightDown = new TranslateAnimation(fromXDelta, toXDelta,
				fromYDelta, toYDelta);
		mAnimRightDown.setDuration(mCateAnimDuration);

		// Animation for ImageView moving up and left (ImageView 3,4 and 5)
		Animation mAnimLeftUp = new TranslateAnimation(fromXDelta, -toXDelta,
				fromYDelta, -toYDelta);
		mAnimLeftUp.setDuration(mCateAnimDuration);

		// Animation for ImageVIew moving Down and left (ImageView 0 and 1)
		Animation mAnimLeftDown = new TranslateAnimation(fromXDelta, -toXDelta,
				fromYDelta, toYDelta);
		mAnimLeftDown.setDuration(mCateAnimDuration);

		// if the category selected is extremeleft or extreme right the the
		// animation will be repeated 2 times to show that two categories are moved
		if (position == 4 || position == 0) {
			mAnimRightUp.setRepeatCount(mCateAnimRepeatCount);
			mAnimRightDown.setRepeatCount(mCateAnimRepeatCount);

			mAnimLeftUp.setRepeatCount(mCateAnimRepeatCount);
			mAnimLeftDown.setRepeatCount(mCateAnimRepeatCount);

		}

		// setting the aniamtion for each ImageView based on its position and category selected
		if (position < 2) {

			mCateImage[0].setAnimation(mAnimRightUp);
			mCateImage[1].setAnimation(mAnimRightUp);
			mCateImage[2].setAnimation(mAnimRightDown);
			mCateImage[3].setAnimation(mAnimRightDown);
			mCateImage[4].setAnimation(mAnimRightDown);
			mCateImage[5].setAnimation(mAnimRightUp);
		}
		// setting the aniamtion for each ImageView based on its position and category selected
		if (position > 2) {

			mCateImage[0].setAnimation(mAnimLeftDown);
			mCateImage[1].setAnimation(mAnimLeftDown);
			mCateImage[2].setAnimation(mAnimLeftDown);
			mCateImage[3].setAnimation(mAnimLeftUp);
			mCateImage[4].setAnimation(mAnimLeftUp);
			mCateImage[5].setAnimation(mAnimLeftUp);
		}

	}// end of method categoryTransition()
	
	
	public Animation mNoteCabinetSwitchAnim(int mAnimNumber){ 
		
		Animation animation = null; 
		if(mAnimNumber == 2)
		{
			animation = new TranslateAnimation(0,0,-50,0);	
			animation.reset();
			animation.setDuration(500);
			
		}else if(mAnimNumber == 1){
			animation = new TranslateAnimation(0, 0, 0, 230); 
			animation.setDuration(800);
		}
		return animation; 
		
	}
	

}