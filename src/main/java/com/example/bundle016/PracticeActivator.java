package com.example.bundle016;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

@Component
public class PracticeActivator {

	private ServiceTracker httpTracker;

	@Activate
	public void start(BundleContext context) throws Exception {
		
		httpTracker = new ServiceTracker(context, HttpService.class.getName(), null) {
			
			public void removedService(ServiceReference reference, Object service) {
				// HTTP service is no longer available, unregister our servlet...
				try {
					((HttpService) service).unregister("/hello");
				} catch (IllegalArgumentException exception) {
					// Ignore; servlet registration probably failed earlier on...
				}
			}

			public Object addingService(ServiceReference reference) {
				// HTTP service is available, register our servlet...
				HttpService httpService = (HttpService) this.context.getService(reference);
				try {
					httpService.registerServlet("/hello", new HelloWorld(), null, null);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				return httpService;
			}
		};
		
		// start tracking all HTTP services...
		httpTracker.open();
	}

	@Deactivate
	public void stop(BundleContext context) throws Exception {
		// stop tracking all HTTP services...
		httpTracker.close();
	}

}
