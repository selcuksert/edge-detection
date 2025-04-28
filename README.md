# Edge Detection with Sobel Method

A high-performance Java application that implements edge detection using the Sobel operator. This project showcases the power of modern Java features by implementing both standard CPU processing and hardware-accelerated processing using TornadoVM.

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

## Building the Project

1. Ensure you have JDK 21 and Maven installed
2. Install TornadoVM following the [official guide](https://github.com/beehive-lab/TornadoVM)
3. Clone this repository
4. Build the project:
    ```shell
    bash mvn clean install
    ```

## Running the Application

### Using Maven

```shell
mvn clean javafx:run
```

### Using JAR file

```shell
java --enable-preview --add-modules=jdk.incubator.vector -jar target/edgedetect-1.0-SNAPSHOT.jar
```

## Implementation Details

### Standard Implementation (CPU)
- Uses Java's Vector API for SIMD operations
- Implements classical Sobel operator algorithm
- Optimized for modern CPU architectures

### TornadoVM Implementation
- Leverages GPU/FPGA acceleration
- Parallel processing of image data
- Dynamic hardware selection based on availability

## Dependencies

- **JavaFX** (21.0.5): UI framework
- **TornadoVM** (1.1.0): Hardware acceleration
- **Apache Commons Math** (4.0-beta1): Mathematical operations
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

- TornadoVM team for the hardware acceleration framework
- JavaFX community for UI components
- OpenJDK team for Vector API development
