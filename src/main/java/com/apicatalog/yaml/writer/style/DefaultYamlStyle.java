package com.apicatalog.yaml.writer.style;

public final class DefaultYamlStyle implements YamlStyle {

    @Override
    public ScalarStyle scalar(YamlStyle.Context context, char[] chars, int offset, int length) {
        // TODO Auto-generated method stub
        return ScalarStyle.FLOW_DOUBLE_QUOTED;
    }

    
}
