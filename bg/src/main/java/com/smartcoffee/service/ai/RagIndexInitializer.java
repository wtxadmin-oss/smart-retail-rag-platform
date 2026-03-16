package com.smartcoffee.service.ai;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class RagIndexInitializer implements ApplicationListener<ContextRefreshedEvent> {
  private static final AtomicBoolean STARTED = new AtomicBoolean(false);

  @Resource private RagChatService ragChatService;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (!STARTED.compareAndSet(false, true)) return;
    try {
      ragChatService.rebuildRagIndex();
    } catch (DataAccessException ignored) {
    } catch (Exception ignored) {
    }
  }
}

