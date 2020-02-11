package com.ldt.navigation.holder;

import androidx.annotation.NonNull;

import com.ldt.navigation.NavigationController;

import java.util.ArrayList;
import java.util.Stack;

public class RouterSaver {
        private boolean mRouterNeedToRestore = true;
        boolean doesRouterNeedToRestore() {
            return  mRouterNeedToRestore;
        }

        void routerRestored() {
            mRouterNeedToRestore = false;
        }

        protected Stack<NavigationController> mControllers = new Stack<>();
        public int count() {
            //updateLast();
            return mControllers.size();
        }

        public NavigationController controllerTop() {
            //updateLast();
            if(mControllers.isEmpty()) return  null;
            return mControllers.lastElement();
        }

        public void clear() {
            mControllers.clear();
        }

        public ArrayList<String> obtainTagList() {
            ArrayList<String> list = new ArrayList<>();
            for (NavigationController c :
                    mControllers) {
                if(c.isControllerAvailable()) list.add(c.mTag);
            }
            return list;
        }

        public NavigationController findController(@NonNull String tag) {
            //updateLast();
            int index = -1;
            int size = mControllers.size();
            for (int i = size - 1; i >= 0 ; i--) {
                if(tag.equals( mControllers.get(i).mTag)) {
                   index = i;
                    break;
                }
            }
            if(index == -1) return null;
            NavigationController controller = mControllers.get(index);
            if(!controller.isControllerAvailable()) {
                mControllers.remove(controller);
                return null;
            }
            return controller;
        }

        public NavigationController controllerAt(int index) {
            return mControllers.get(index);
        }

        public void pop() {
            mControllers.pop();
        }

        public void push(NavigationController controller) {
            if(mControllers.indexOf(controller)==-1) mControllers.push(controller);
        }

    public void remove(NavigationController controller) {
        mControllers.remove(controller);
    }
}