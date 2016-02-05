package org.droitateddb;

import android.content.Context;

import org.droitateddb.config.Persistence;

import java.lang.annotation.Annotation;

/**
 * Utility for setup. Register the {@link org.droitateddb.config.Persistence} annotated class in a static block in your {@link  android.app.Application}
 *
 *    {@code
public class MyApplication extends Application {
static {
DroitatedDB.init(MyDatabase.class);
}
...
}
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public class DroitatedDB {
	private static String basePackage;

	private DroitatedDB() {
		//static only
	}

	/**
	 * Register #classWithPersistenceAnnotation
	 * @param classWithPersistenceAnnotation - {@link org.droitateddb.config.Persistence} annotated class
	 */
	public static void init(final Class<?> classWithPersistenceAnnotation) {
		Annotation[] annotations = classWithPersistenceAnnotation.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation.annotationType().equals(Persistence.class)) {
				basePackage = ((Persistence) annotation).basePackage();
			}
		}
		throwExceptionWhenInitNotSuccesfull();
	}

	private static void throwExceptionWhenInitNotSuccesfull() {
		if (basePackage == null) {
			throw new IllegalStateException("DroitatedDB init failed");
		}
	}

	static String getBasePackage(Context context) {
		if (basePackage == null || "".equals(basePackage)) {
			basePackage = context.getPackageName();
		}
		return basePackage;
	}

}
