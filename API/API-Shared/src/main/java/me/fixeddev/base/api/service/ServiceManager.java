package me.fixeddev.base.api.service;

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@ImplementedBy(ServiceManager.ServiceManagerImpl.class)
public interface ServiceManager extends Service {

    @Singleton
    class ServiceManagerImpl implements ServiceManager, AbstractService {

        @Inject
        private Set<Service> services;

        private AtomicBoolean started;

        ServiceManagerImpl() {
            this.started = new AtomicBoolean();
        }

        @Override
        public void doStart() throws Exception {
            for (Service service : services) {
                try {
                    service.start();
                } catch (InterruptedException | ThreadDeath ex){
                    throw ex;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        @Override
        public void doStop() {
            for (Service service : services) {
                service.stop();
            }
        }

        @Override
        public AtomicBoolean isStarted() {
            return started;
        }
    }
}
