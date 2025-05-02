# Edge Detection with Sobel Method

A high-performance Java application that implements edge detection using the Sobel operator. This project showcases the power of modern Java features by implementing both standard CPU processing and hardware-accelerated processing using [TornadoVM](https://www.tornadovm.org/).

![snapshot](/docs/snapshot.png)

The project is implemented as a PoC for Medium article [Accelerating Java with TornadoVM](https://medium.com/@selcuk.sert/accelerating-java-with-tornadovm-14e3c8b2695f).

## Overview

This application provides edge detection functionality using the Sobel operator with two different implementation approaches:
- Standard CPU-based processing (`SobelStandard`)
- Hardware-accelerated processing using TornadoVM (`SobelTornado`)

The application features a JavaFX-based GUI for easy interaction and real-time image processing visualization.

## Prerequisites

- Java Development Kit (JDK) 21 or later
- Maven 3.x
- TornadoVM 1.1.0 runtime
- Graphics card supporting OpenCL (for TornadoVM acceleration)

## Installing TornadoVM

[Instructions](https://tornadovm.readthedocs.io/en/latest/installation.html#installation-configuration) from the official site can be used.

As an example for existing Eclipse Temurin JDK 21 on a macOS:

1. Clone TornadoVM repo and run installation script:
   ```shell
   cd /usr/local/share/
   git clone https://github.com/beehive-lab/TornadoVM.git
   cd TornadoVM
   ./bin/tornadovm-installer --jdk temurin-jdk-21 --javaHome $JAVA_HOME --backend opencl
   ```
2. The following prompt should be displayed on console:
   ```text
   ###########################################################################
   Tornado build success
   Updating PATH and TORNADO_SDK to tornado-sdk-....
   Commit  : ....
   ###########################################################################
   ------------------------------------------
          TornadoVM installation done        
   ------------------------------------------
   Creating source file ......................
   ........................................[ok]
   ```
3. Append the following line to `~/.zshrc` for environment settings:
   ```text
   # Set TornadoVM variables
   source /usr/local/share/TornadoVM/setvars.sh
   ```
4. On a new terminal shell list accelerator devices available to TornadoVM:
   ```shell
   tornado --devices
   ```
   An output like this should be displayed:
   ```text
   Number of Tornado drivers: 1
   Driver: OpenCL
   Total number of OpenCL devices  : 3
   Tornado device=0:0  (DEFAULT)
   OPENCL --  [Apple] -- Intel(R) UHD Graphics 630
   Global Memory Size: 1,5 GB
   Local Memory Size: 64,0 KB
   Workgroup Dimensions: 3
   Total Number of Block Threads: [256]
   Max WorkGroup Configuration: [256, 256, 256]
   Device OpenCL C version: OpenCL C 1.2
   
   Tornado device=0:1
   OPENCL --  [Apple] -- AMD Radeon Pro 5500M Compute Engine
   Global Memory Size: 4,0 GB
   Local Memory Size: 64,0 KB
   Workgroup Dimensions: 3
   Total Number of Block Threads: [256]
   Max WorkGroup Configuration: [256, 256, 256]
   Device OpenCL C version: OpenCL C 1.2
   
   Tornado device=0:2
   OPENCL --  [Apple] -- Intel(R) Core(TM) i9-9880H CPU @ 2.30GHz
   Global Memory Size: 32,0 GB
   Local Memory Size: 32,0 KB
   Workgroup Dimensions: 3
   Total Number of Block Threads: [1024]
   Max WorkGroup Configuration: [1024, 1, 1]
   Device OpenCL C version: OpenCL C 1.2
   ```
5. Run TornadoVM unit tests with the following JVM parameters as a workaround if you are using Turkish locale ([issue](https://github.com/beehive-lab/TornadoVM/issues/605) filed to project):
   ```shell
   tornado-test -V --jvm="-Duser.country=US -Duser.language=en"
   ```
   Run a specific example to test installation:
      ```shell
      tornado --jvm="-Duser.country=US -Duser.language=en" -m tornado.examples/uk.ac.manchester.tornado.examples.compute.MonteCarlo
      ```

## Features

- **Dual Implementation**: Choose between standard CPU processing or TornadoVM acceleration
- **Modern Java Features**:
    - Vector API (Preview feature) for optimized CPU processing
    - Module system integration
    - Latest JavaFX controls and features
- **Real-time Processing**: Live preview of edge detection results
- **Hardware Acceleration**: TornadoVM support for GPU/FPGA acceleration
- **Configurable Parameters**: Adjust Sobel operator parameters in real-time
- **Comprehensive Logging**: Detailed logging using SLF4J and Logback

## Running the Application

### Using IDE

To run a TornadoVM app, one should use output of `tornado --printJavaFlags` CLI command. The following lines should be modified as this project is a JavaFx app.

* Install JavaFx runtime to your PC and prepend the lib path to module path setting:
   ```text
   --module-path [JAVA_FX_PATH/lib]:[TORNADO CLI COMMAND MODULE PATH]
   ```

* Add modules for JavaFX:
   ``` text
   --add-modules [TORNADO CLI COMMAND MODULES],javafx.controls,javafx.fxml
   ```

* Add the following as a workaround for host with Turkish locale
   ``` text
   -Duser.country=EN
   -Duser.language=us
   ```

An example VM option set:

```text
-server
-XX:-UseCompressedOops
-XX:+UnlockExperimentalVMOptions
-XX:+EnableJVMCI
-XX:-UseCompressedClassPointers
--enable-preview
-Djava.library.path=/usr/local/share/TornadoVM/bin/sdk/lib
--module-path
/usr/local/share/JavaFX/javafx-sdk-21.0.7/lib:/usr/local/share/TornadoVM/bin/sdk/share/java/tornado
-Dtornado.load.api.implementation=uk.ac.manchester.tornado.runtime.tasks.TornadoTaskGraph
-Dtornado.load.runtime.implementation=uk.ac.manchester.tornado.runtime.TornadoCoreRuntime
-Dtornado.load.tornado.implementation=uk.ac.manchester.tornado.runtime.common.Tornado
-Dtornado.load.annotation.implementation=uk.ac.manchester.tornado.annotation.ASMClassVisitor
-Dtornado.load.annotation.parallel=uk.ac.manchester.tornado.api.annotations.Parallel
--upgrade-module-path
/usr/local/share/TornadoVM/bin/sdk/share/java/graalJars
-XX:+UseParallelGC
@/usr/local/share/TornadoVM/bin/sdk/etc/exportLists/common-exports
@/usr/local/share/TornadoVM/bin/sdk/etc/exportLists/opencl-exports
--add-modules
ALL-SYSTEM,tornado.runtime,tornado.annotation,tornado.drivers.common,tornado.drivers.opencl,javafx.controls,javafx.fxml
-Dtornado.debug=False
-Dtornado.printKernel=False
-Dtornado.threadInfo=False
-Duser.country=EN
-Duser.language=us
```

## Implementation Details

### Standard Implementation (CPU)
- Implements classical Sobel operator algorithm

### TornadoVM Implementation
- Leverages GPU/FPGA acceleration
- Parallel processing of image data
- Dynamic hardware selection based on availability

## Dependencies

- **JavaFX** (21.0.5): UI framework
- **TornadoVM** (1.1.0): Hardware acceleration
- **ControlsFX** (11.2.1): Enhanced JavaFX controls
- **SLF4J/Logback** (2.0.17/1.5.17): Logging framework
- **JUnit** (5.10.2): Testing framework

## Configuration

### Logging
- Log files are stored in the `logs` directory
- Main log file: `data.log`
- Configure logging levels in `logback.xml`

### TornadoVM Settings
- Default device selection: First available GPU
- Fallback to CPU if no GPU is available
- Configure TornadoVM settings through environment variables

## Performance Considerations

- GPU acceleration provides significant speedup for large images
- Vector API optimization improves CPU performance
- Memory usage is optimized for both implementations

## Troubleshooting

### Common Issues

1. **TornadoVM not detected**
    - Ensure TornadoVM is properly installed
    - Check OpenCL installation
    - Verify GPU drivers are up to date

2. **Performance Issues**
    - Try switching between implementations
    - Check system resource usage
    - Verify image size constraints

### Error Logging

Check the `logs/data.log` file for detailed error information and stack traces.

## Future Enhancements

- [ ] Add support for additional edge detection algorithms
- [ ] Implement batch processing capability
- [ ] Add more hardware acceleration options
- [ ] Enhance UI with additional controls and features

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Acknowledgments

- [TornadoVM](https://www.tornadovm.org/) team for the acceleration framework
- JavaFX community for UI components
