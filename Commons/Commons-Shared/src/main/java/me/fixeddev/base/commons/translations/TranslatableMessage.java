package me.fixeddev.base.commons.translations;

import org.jetbrains.annotations.NotNull;

/**
 * This interface represents a message that can be translated
 * The only requirement for a translatable message is that the variables
 * will not change depending of the language, because the values for every
 * variable are defined before the message for the specific language is created
 */
public interface TranslatableMessage {
    /**
     * This method as mentioned above returns the path of this message
     * Also, this path is a identifier for this specific translatable message
     * since the path will be the same on all the languages
     * @return The path in the config for this specific message
     */
    @NotNull
    String getPath();

    /**
     * Retrieves the variable values that are set and parses the
     * raw message for the specified lang returning a translated message
     * with the specified variables replaced
     * @param lang The language of the message
     * @return A translated message, if the message doesn't exist for the specific
     * language, then returns the default language parsed message
     */
    @NotNull
    String getMessageForLang(@NotNull String lang);

    /**
     * Sets a variable identified with the specified key to be replaced
     * with the specified value
     * @param key The specific key of the variable to replace for example:
     *            The key "test" will replace the in-text variable "{test}"
     * @param value The value to replace the in-text variable with:
     *              A variable with key "test" and value "foo" will replace
     *              the in-text variable "{test}" with "foo"
     * @return This same object or a copy of it, depending of the implementation.
     *          If the method {@link {isImmutable}} returns true then it's a copy of it, otherwise
     *          the same object
     */
    @NotNull
    TranslatableMessage setVariableValue(String key, String value);

    @NotNull
    default TranslatableMessage setVariableValue(int key, String value){
        return setVariableValue(key + "", value);
    }

    boolean isImmutable();
}
