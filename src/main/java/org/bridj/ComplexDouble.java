package org.bridj;
import org.bridj.ann.*;

public class ComplexDouble extends StructObject {
    @Field(0) 
    public double real() {
        return this.io.getDoubleField(this, 0);
    }
    @Field(0) 
    public ComplexDouble real(double real) {
        this.io.setDoubleField(this, 0, real);
        return this;
    }
    @Field(1) 
    public double imag() {
        return this.io.getDoubleField(this, 0);
    }
    @Field(1) 
    public ComplexDouble imag(double imag) {
        this.io.setDoubleField(this, 0, imag);
        return this;
    }
}
