package org.droitateddb.hooks;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks the hook for the database create to be implemented in.<br>
 * The class also has to implement the {@link DbCreate} interface.
 *
 * @author Alexander Frank
 * @author Falk Appel
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Create {
	// marker
}
