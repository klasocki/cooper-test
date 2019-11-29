package com.example.coopertest

import android.app.Application
import android.content.Context

class App (context : Context) : Application() {

    private val mContext : Context = context ;

    fun getContext() : Context {
        return mContext;
    }

  /* fun setContext( mContext: Context) {
        this.mContext = mContext;
    }*/

}