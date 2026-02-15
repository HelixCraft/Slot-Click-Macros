#!/bin/bash

# Script to test Minecraft versions 1.21 through 1.21.11

echo "Starting Minecraft version build tests..."
echo "=========================================="
echo ""

# Array to store results
declare -a successful_versions
declare -a failed_versions

# Loop through versions 1.21 to 1.21.11
for i in {5..11}; do
    if [ $i -eq 0 ]; then
        version="1.21"
    else
        version="1.21.$i"
    fi
    
    echo "Testing Minecraft version: $version"
    echo "-----------------------------------"
    
    # Get current mod version (base version without +mc suffix)
    base_mod_version=$(grep "^mod_version=" gradle.properties | cut -d'=' -f2 | sed 's/+mc.*//')
    
    # Update gradle.properties with new versions
    sed -i "s/^minecraft_version=.*/minecraft_version=$version/" gradle.properties
    sed -i "s/^mod_version=.*/mod_version=${base_mod_version}+mc${version}/" gradle.properties
    
    # Run gradle build and show output
    ./gradlew build
    
    # Check if build was successful
    if [ $? -eq 0 ]; then
        echo "✓ SUCCESS: Minecraft $version built successfully!"
        successful_versions+=("$version")
    else
        echo "✗ FAILED: Minecraft $version build failed"
        failed_versions+=("$version")
    fi
    
    echo ""
done

# Print summary
echo "=========================================="
echo "BUILD TEST SUMMARY"
echo "=========================================="
echo ""

if [ ${#successful_versions[@]} -gt 0 ]; then
    echo "Successful builds (${#successful_versions[@]}):"
    for ver in "${successful_versions[@]}"; do
        echo "  ✓ Minecraft $ver"
    done
    echo ""
fi

if [ ${#failed_versions[@]} -gt 0 ]; then
    echo "Failed builds (${#failed_versions[@]}):"
    for ver in "${failed_versions[@]}"; do
        echo "  ✗ Minecraft $ver"
    done
    echo ""
fi

echo "Testing complete!"
