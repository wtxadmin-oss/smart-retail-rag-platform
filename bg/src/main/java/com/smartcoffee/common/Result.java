package com.smartcoffee.common;

public class Result<T> {
  private int code;
  private String message;
  private T data;

  public int getCode() { return code; }
  public void setCode(int code) { this.code = code; }
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }
  public T getData() { return data; }
  public void setData(T data) { this.data = data; }

  public static <T> Result<T> ok(T data) {
    Result<T> r = new Result<>();
    r.code = 0;
    r.message = "OK";
    r.data = data;
    return r;
  }

  public static <T> Result<T> ok() {
    Result<T> r = new Result<>();
    r.code = 0;
    r.message = "OK";
    return r;
  }

  public static <T> Result<T> fail(int code, String message) {
    Result<T> r = new Result<>();
    r.code = code;
    r.message = message;
    return r;
  }

  public static class PageData<T> {
    private long total;
    private java.util.List<T> list;
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public java.util.List<T> getList() { return list; }
    public void setList(java.util.List<T> list) { this.list = list; }
  }

  public static <T> Result<PageData<T>> page(long total, java.util.List<T> list) {
    PageData<T> pd = new PageData<>();
    pd.setTotal(total);
    pd.setList(list);
    return Result.ok(pd);
  }
}
