package com.ldt.navigation.container;

import androidx.annotation.NonNull;

import com.ldt.navigation.NavigationControllerFragment;

import java.util.ArrayList;
import java.util.Stack;

public class NavigatorAttribute {
        private boolean mRouterNeedToRestore = true;
        boolean doesRouterNeedToRestore() {
            return  mRouterNeedToRestore;
        }

        void routerRestored() {
            mRouterNeedToRestore = false;
        }

        protected Stack<NavigationControllerFragment> mControllers = new Stack<>();
        public int count() {
            //updateLast();
            return mControllers.size();
        }

        public NavigationControllerFragment controllerTop() {
            //updateLast();
            if(mControllers.isEmpty()) return  null;
            return mControllers.lastElement();
        }

        public void clear() {
            mControllers.clear();
        }

        public ArrayList<String> obtainTagList() {
            ArrayList<String> list = new ArrayList<>();
            for (NavigationControllerFragment c :
                    mControllers) {
                if(c.isControllerAvailable()) list.add(c.mControllerTag);
            }
            return list;
        }

        public NavigationControllerFragment findController(@NonNull String tag) {
            //updateLast();
            int index = -1;
            int size = mControllers.size();
            for (int i = size - 1; i >= 0 ; i--) {
                if(tag.equals( mControllers.get(i).mControllerTag)) {
                   index = i;
                    break;
                }
            }
            if(index == -1) return null;
            NavigationControllerFragment controller = mControllers.get(index);
            if(!controller.isControllerAvailable()) {
                mControllers.remove(controller);
                return null;
            }
            return controller;
        }

        public NavigationControllerFragment controllerAt(int index) {
            return mControllers.get(index);
        }

        public void pop() {
            mControllers.pop();
        }

        public void push(NavigationControllerFragment controller) {
            if(mControllers.indexOf(controller)==-1) mControllers.push(controller);
        }

    public void remove(NavigationControllerFragment controller) {
        mControllers.remove(controller);
    }
}