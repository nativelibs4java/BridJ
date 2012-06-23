package org.bridj;

import org.junit.Test;
import static org.junit.Assert.*;

import org.bridj.ann.Field;
import org.bridj.ann.Library;
import org.bridj.ann.Array;
import org.bridj.ann.Ptr;
import org.bridj.ann.Struct;
import static org.bridj.Pointer.*;
import static org.bridj.BridJ.*;

@Library("test")
public class NASATest {
	static {
		BridJ.register();
	}
	public static class stuff extends StructObject {
		public stuff() {
			super();
		}
		/// C type : char[100]
		@Array({100}) 
		@Field(0) 
		public Pointer<Byte > part1() {
			return this.io.getPointerField(this, 0);
		}
		/// C type : char[100]
		@Array({100}) 
		@Field(1) 
		public Pointer<Byte > part2() {
			return this.io.getPointerField(this, 1);
		}
		/// C type : char[100]
		@Array({100}) 
		@Field(2) 
		public Pointer<Byte > part3() {
			return this.io.getPointerField(this, 2);
		}
		public stuff(Pointer pointer) {
			super(pointer);
		}
	};
	public static class container extends StructObject {
		public container() {
			super();
		}
		/// C type : char[100]
		@Array({100}) 
		@Field(0) 
		public Pointer<Byte > type() {
			return this.io.getPointerField(this, 0);
		}
		/// C type : char[100]
		@Array({100}) 
		@Field(1) 
		public Pointer<Byte > date() {
			return this.io.getPointerField(this, 1);
		}
		/// C type : char[100]
		@Array({100}) 
		@Field(2) 
		public Pointer<Byte > time() {
			return this.io.getPointerField(this, 2);
		}
		/// C type : stuff
		@Field(3) 
		public stuff stuff1() {
			return this.io.getNativeObjectField(this, 3);
		}
		/// C type : stuff
		@Field(3) 
		public container stuff1(stuff stuff1) {
			this.io.setNativeObjectField(this, 3, stuff1);
			return this;
		}
		/// C type : stuff
		public final stuff stuff1_$eq(stuff stuff1) {
			stuff1(stuff1);
			return stuff1;
		}
		/// C type : stuff
		@Field(4) 
		public stuff stuff2() {
			return this.io.getNativeObjectField(this, 4);
		}
		/// C type : stuff
		@Field(4) 
		public container stuff2(stuff stuff2) {
			this.io.setNativeObjectField(this, 4, stuff2);
			return this;
		}
		/// C type : stuff
		public final stuff stuff2_$eq(stuff stuff2) {
			stuff2(stuff2);
			return stuff2;
		}
		public container(Pointer pointer) {
			super(pointer);
		}
	};
	public static class message extends StructObject {
		public message() {
			super();
		}
		/// C type : char[512]
		@Array({512}) 
		@Field(0) 
		public Pointer<Byte > text() {
			return this.io.getPointerField(this, 0);
		}
		/// C type : container[1]
		@Array({1}) 
		@Field(1) 
		public Pointer<container > container() {
			return this.io.getPointerField(this, 1);
		}
		public message(Pointer pointer) {
			super(pointer);
		}
	};
	
	public static native void Connect(Pointer<message> msg);
	
	@Test
	public void test() {
       message msg = new message();

       msg.text().setCString("this is a test string from java");

       Pointer<message> Pmsg = Pointer.pointerTo(msg);
       Connect(Pmsg);

       int i = 0;
       for (container object : msg.container())
       {
               System.out.println("Object " + i++);
               System.out.println("Type = " + object.type().getCString());
       }

       System.out.println("return message = " + msg.text().getCString());
    }
}
