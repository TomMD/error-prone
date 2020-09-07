/*
 * Copyright 2012 The Error Prone Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.errorprone.bugpatterns.testdata;

import com.google.errorprone.annotations.DangerouslySuppressBanSerializable;
import com.google.errorprone.annotations.SuppressBanSerializableCompletedSecurityReview;
import com.google.errorprone.annotations.SuppressBanSerializableForLegacyCode;
import com.google.errorprone.bugpatterns.BanSerializableReadTest;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * {@link BanSerializableReadTest}
 *
 * @author tshadwell@google.com (Thomas Shadwell)
 */
public class BanSerializableReadNegativeCases {
  public final String hi = "hi";

  // mostly a smoke test
  public static final void noCrimesHere() {
    System.out.println(new BanSerializableReadNegativeCases().hi);
  }

  // just a readObject with no call to it is OK
  public void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
    ois.defaultReadObject();
  }

  /**
   * The checker has a special whitelist that allows classes to define methods called readObject --
   * Java accepts these as an override to the default serialization behaviour. While we want to
   * allow such methods to be defined, we don't want to allow these methods to be called.
   *
   * <p>this version has the checks suppressed
   *
   * @throws IOException
   * @throws ClassNotFoundException
   */
  @DangerouslySuppressBanSerializable
  public static final void directCall() throws IOException, ClassNotFoundException {
    PipedInputStream in = new PipedInputStream();
    PipedOutputStream out = new PipedOutputStream(in);

    ObjectOutputStream serializer = new ObjectOutputStream(out);
    ObjectInputStream deserializer = new ObjectInputStream(in);

    BanSerializableReadPositiveCases self = new BanSerializableReadPositiveCases();
    self.readObject(deserializer);
  }

  /**
   * Says 'hi' by piping the string value unsafely through Object I/O stream. This one has the check
   * suppressed, though
   *
   * @throws IOException
   * @throws ClassNotFoundException
   */
  @DangerouslySuppressBanSerializable
  public static final void sayHi() throws IOException, ClassNotFoundException {
    PipedInputStream in = new PipedInputStream();
    PipedOutputStream out = new PipedOutputStream(in);

    ObjectOutputStream serializer = new ObjectOutputStream(out);
    ObjectInputStream deserializer = new ObjectInputStream(in);

    serializer.writeObject(new BanSerializableReadPositiveCases());
    serializer.close();

    BanSerializableReadPositiveCases crime =
        (BanSerializableReadPositiveCases) deserializer.readObject();
    System.out.println(crime.hi);
  }

  // These test the more esoteric annotations

  // code has gone through a security review
  @SuppressBanSerializableCompletedSecurityReview
  public static final void directCall2() throws IOException, ClassNotFoundException {
    PipedInputStream in = new PipedInputStream();
    PipedOutputStream out = new PipedOutputStream(in);

    ObjectOutputStream serializer = new ObjectOutputStream(out);
    ObjectInputStream deserializer = new ObjectInputStream(in);

    BanSerializableReadPositiveCases self = new BanSerializableReadPositiveCases();
    self.readObject(deserializer);
  }

  // code is well-tested legacy
  @SuppressBanSerializableForLegacyCode
  public static final void directCall3() throws IOException, ClassNotFoundException {
    PipedInputStream in = new PipedInputStream();
    PipedOutputStream out = new PipedOutputStream(in);

    ObjectOutputStream serializer = new ObjectOutputStream(out);
    ObjectInputStream deserializer = new ObjectInputStream(in);

    BanSerializableReadPositiveCases self = new BanSerializableReadPositiveCases();
    self.readObject(deserializer);
  }
}