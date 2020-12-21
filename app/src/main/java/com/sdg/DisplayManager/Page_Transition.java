
package com.sdg.DisplayManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class Page_Transition {
	/** Called when the activity is first created. */

	public void PageTrans_Animation(RelativeLayout panel_current,
			RelativeLayout panel_next, int effect) {
		/* animation object */
		Animation animation;

		/*
		 * animations sets for the creating a group of animation into a single
		 * effect
		 */
		AnimationSet set_in;
		AnimationSet set_out;

		/* animation controlers for different animations */
		LayoutAnimationController controller;
		LayoutAnimationController controller2;

		/* different animation durations */
		
		int aplha_anim_dur2 = 1200;
		int trans_anim_dur2 = 1500;
		int aplha_anim_dur1 = 1200;
		int trans_anim_dur1 = 1500;
		/* creating Animation, Animation Set and declaring parameters */
		set_in = new AnimationSet(true);
		set_out = new AnimationSet(true);

		if (effect == 0)/* vertical transition */
		{
			/*
			 * //fade in animation = new AlphaAnimation(0.0f, 1.0f);
			 * animation.setDuration(aplha_anim_dur1);
			 * set_in.addAnimation(animation); set_in.setFillAfter(true);
			 * 
			 * //slide in horizontally animation = new TranslateAnimation(
			 * Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
			 * 0.0f, Animation.RELATIVE_TO_SELF, 0.2f,
			 * Animation.RELATIVE_TO_SELF, 0.0f
			 * 
			 * ); animation.setDuration(trans_anim_dur1);
			 * set_in.addAnimation(animation); set_in.setFillAfter(true);
			 * 
			 * //fade out animation = new AlphaAnimation(1.0f, 0.0f);
			 * animation.setDuration(aplha_anim_dur2);
			 * set_out.addAnimation(animation); set_out.setFillAfter(true);
			 * 
			 * //slide out horizontally animation = new TranslateAnimation(
			 * Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
			 * 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
			 * Animation.RELATIVE_TO_SELF, -0.2f
			 * 
			 * ); animation.setDuration(trans_anim_dur2);
			 * set_out.addAnimation(animation); set_out.setFillAfter(true);
			 * 
			 * controller2 = new LayoutAnimationController( set_out, 0.00f);
			 * panel_next.setLayoutAnimation(controller2);
			 * 
			 * controller = new LayoutAnimationController( set_in, 0.00f);
			 * panel_current.setLayoutAnimation(controller);
			 */
		} else if (effect == 1)/* effect = 1 , horizontal trnasition */
		{

			// fade in
			animation = new AlphaAnimation(0.0f, 1.0f);
			animation.setDuration(aplha_anim_dur1);
			set_in.addAnimation(animation);
			set_in.setFillAfter(true);
			// slide in horizontally
			animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
					0.2f, Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f);
			animation.setDuration(trans_anim_dur1);
			set_in.addAnimation(animation);
			set_in.setFillAfter(true);

			// fade out
			animation = new AlphaAnimation(1.0f, 0.0f);
			animation.setDuration(aplha_anim_dur2);
			set_out.addAnimation(animation);
			set_out.setFillAfter(true);

			// slide out horizontally
			animation = new TranslateAnimation(

			Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
					-0.2f, Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f);
			animation.setDuration(trans_anim_dur2);
			set_out.addAnimation(animation);
			set_out.setFillAfter(true);

			controller2 = new LayoutAnimationController(set_out, 0.00f);
			panel_next.setLayoutAnimation(controller2);

			controller = new LayoutAnimationController(set_in, 0.00f);
			panel_current.setLayoutAnimation(controller);

		} else if (effect == 2) {
			// fade in
			animation = new AlphaAnimation(0.0f, 1.0f);
			animation.setDuration(aplha_anim_dur1);
			set_in.addAnimation(animation);
			set_in.setFillAfter(true);
			// slide in horizontally
			animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
					-0.2f, Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f);
			animation.setDuration(trans_anim_dur1);
			set_in.addAnimation(animation);
			set_in.setFillAfter(true);

			// fade out
			animation = new AlphaAnimation(1.0f, 0.0f);
			animation.setDuration(aplha_anim_dur2);
			set_out.addAnimation(animation);
			set_out.setFillAfter(true);

			// slide out horizontally
			animation = new TranslateAnimation(

			Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.2f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f);
			animation.setDuration(trans_anim_dur2);
			set_out.addAnimation(animation);
			set_out.setFillAfter(true);

			controller2 = new LayoutAnimationController(set_out, 0.00f);
			panel_next.setLayoutAnimation(controller2);

			controller = new LayoutAnimationController(set_in, 0.00f);
			panel_current.setLayoutAnimation(controller);

		}
	
	}

}
