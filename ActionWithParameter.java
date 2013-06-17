import java.lang.reflect.Method;
import java.lang.RuntimeException;
import java.util.LinkedList;
import java.util.List;

import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;

/**
 * This is use in jmock, if you used some other mock framework just need a little change.
 */
public class ActionWithParameter extends CustomAction {
    private static final String HANDLE_METHOD_NAME = "handle";

    private Method handler;
    private int[] parameterFilter;

    public ActionWithParameter(int ... parameterFilter) {
        super("ActionWithParameter");
        for (Method m : this.getClass().getDeclaredMethods()) {
            if (m.getName().equals(HANDLE_METHOD_NAME)) {
                handler = m;
                if (parameterFilter.length != 0
                        && handler.getParameterTypes().length != parameterFilter.length) {
                    throw new RuntimeException("handler parameter number wrong, you implemention has "
                            + handler.getParameterTypes().length
                            + " but you need is " + parameterFilter.length);
                }
            }
        }
        if (handler == null) {
            throw new RuntimeException("no handler with method name: " + HANDLE_METHOD_NAME);
        }
        this.parameterFilter = parameterFilter;
        handler.setAccessible(true);
    }

    @Override
    public Object invoke(Invocation invocation) throws Throwable {
        Object[] param = null;
        if (parameterFilter.length == 0) {
            param = invocation.getParametersAsArray();
        } else {
            List<Object> parameter = new LinkedList<Object>();
            for (int i : parameterFilter) {
                parameter.add(invocation.getParameter(i));
            }
            param = parameter.toArray();
        }
        return handler.invoke(this, param);
    }
}
