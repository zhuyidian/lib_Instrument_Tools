/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.coocaa.remoteplatform.core.service;
// Declare any non-default types here with import statements

public interface IRemotePlatformClientCallback extends android.os.IInterface
{
  /** Default implementation for IRemotePlatformClientCallback. */
  public static class Default implements com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback
  {
    /**
         * Demonstrates some basic types that you can use as parameters
         * and return values in AIDL.
         */
    @Override public java.util.Map invokeCallBackMethod(java.lang.String method, java.util.Map params) throws android.os.RemoteException
    {
      return null;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback
  {
    private static final java.lang.String DESCRIPTOR = "com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback interface,
     * generating a proxy if needed.
     */
    public static com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback))) {
        return ((com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback)iin);
      }
      return new com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_invokeCallBackMethod:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.util.Map _arg1;
          java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
          _arg1 = data.readHashMap(cl);
          java.util.Map _result = this.invokeCallBackMethod(_arg0, _arg1);
          reply.writeNoException();
          reply.writeMap(_result);
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      /**
           * Demonstrates some basic types that you can use as parameters
           * and return values in AIDL.
           */
      @Override public java.util.Map invokeCallBackMethod(java.lang.String method, java.util.Map params) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.Map _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(method);
          _data.writeMap(params);
          boolean _status = mRemote.transact(Stub.TRANSACTION_invokeCallBackMethod, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().invokeCallBackMethod(method, params);
          }
          _reply.readException();
          java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
          _result = _reply.readHashMap(cl);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      public static com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback sDefaultImpl;
    }
    static final int TRANSACTION_invokeCallBackMethod = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    public static boolean setDefaultImpl(com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Stub.Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Stub.Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  /**
       * Demonstrates some basic types that you can use as parameters
       * and return values in AIDL.
       */
  public java.util.Map invokeCallBackMethod(java.lang.String method, java.util.Map params) throws android.os.RemoteException;
}
