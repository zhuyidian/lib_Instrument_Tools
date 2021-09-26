/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.coocaa.remoteplatform.core.service;
public interface IRemotePlatformService extends android.os.IInterface
{
  /** Default implementation for IRemotePlatformService. */
  public static class Default implements com.coocaa.remoteplatform.core.service.IRemotePlatformService
  {
    @Override public com.coocaa.remoteplatform.core.service.AttachInfo getAttachInfo() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.Map invokeMethod(java.lang.String method, java.util.Map params) throws android.os.RemoteException
    {
      return null;
    }
    @Override public void setClientCallback(java.lang.String packageName, com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback callback) throws android.os.RemoteException
    {
    }
    @Override public void clearClientCallback(com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback callback) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.coocaa.remoteplatform.core.service.IRemotePlatformService
  {
    private static final java.lang.String DESCRIPTOR = "com.coocaa.remoteplatform.core.service.IRemotePlatformService";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.coocaa.remoteplatform.core.service.IRemotePlatformService interface,
     * generating a proxy if needed.
     */
    public static com.coocaa.remoteplatform.core.service.IRemotePlatformService asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.coocaa.remoteplatform.core.service.IRemotePlatformService))) {
        return ((com.coocaa.remoteplatform.core.service.IRemotePlatformService)iin);
      }
      return new com.coocaa.remoteplatform.core.service.IRemotePlatformService.Stub.Proxy(obj);
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
        case TRANSACTION_getAttachInfo:
        {
          data.enforceInterface(descriptor);
          com.coocaa.remoteplatform.core.service.AttachInfo _result = this.getAttachInfo();
          reply.writeNoException();
          if ((_result!=null)) {
            reply.writeInt(1);
            _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          }
          else {
            reply.writeInt(0);
          }
          return true;
        }
        case TRANSACTION_invokeMethod:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.util.Map _arg1;
          java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
          _arg1 = data.readHashMap(cl);
          java.util.Map _result = this.invokeMethod(_arg0, _arg1);
          reply.writeNoException();
          reply.writeMap(_result);
          return true;
        }
        case TRANSACTION_setClientCallback:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback _arg1;
          _arg1 = com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback.Stub.asInterface(data.readStrongBinder());
          this.setClientCallback(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_clearClientCallback:
        {
          data.enforceInterface(descriptor);
          com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback _arg0;
          _arg0 = com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback.Stub.asInterface(data.readStrongBinder());
          this.clearClientCallback(_arg0);
          reply.writeNoException();
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.coocaa.remoteplatform.core.service.IRemotePlatformService
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
      @Override public com.coocaa.remoteplatform.core.service.AttachInfo getAttachInfo() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        com.coocaa.remoteplatform.core.service.AttachInfo _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAttachInfo, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAttachInfo();
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = com.coocaa.remoteplatform.core.service.AttachInfo.CREATOR.createFromParcel(_reply);
          }
          else {
            _result = null;
          }
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.Map invokeMethod(java.lang.String method, java.util.Map params) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.Map _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(method);
          _data.writeMap(params);
          boolean _status = mRemote.transact(Stub.TRANSACTION_invokeMethod, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().invokeMethod(method, params);
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
      @Override public void setClientCallback(java.lang.String packageName, com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setClientCallback, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setClientCallback(packageName, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void clearClientCallback(com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_clearClientCallback, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().clearClientCallback(callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      public static com.coocaa.remoteplatform.core.service.IRemotePlatformService sDefaultImpl;
    }
    static final int TRANSACTION_getAttachInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_invokeMethod = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_setClientCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_clearClientCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    public static boolean setDefaultImpl(com.coocaa.remoteplatform.core.service.IRemotePlatformService impl) {
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
    public static com.coocaa.remoteplatform.core.service.IRemotePlatformService getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public com.coocaa.remoteplatform.core.service.AttachInfo getAttachInfo() throws android.os.RemoteException;
  public java.util.Map invokeMethod(java.lang.String method, java.util.Map params) throws android.os.RemoteException;
  public void setClientCallback(java.lang.String packageName, com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback callback) throws android.os.RemoteException;
  public void clearClientCallback(com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback callback) throws android.os.RemoteException;
}
