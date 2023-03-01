1. Follow instructions in https://ranger.apache.org/quick_start_guide.html to clone ranger and build
2. Add module ranger-legend-plugin to project (copy directory, add module to parent pom, and add reference to parent in ranger-legend-plugin/pom.xml)
3. Copy ranger-plugins-common/ranger-servicedef-legend.json and copy to {ranger folder}/agents-common/src/main/resources/service-defs 
4. Update EmbeddedServiceDefsUtil in {ranger folder}/agents-common/src/main/java/org/apache/ranger/plugins/store to include legendServiceDef
5. Update {ranger folder}/distro/src/main/assembly/admin-web.xml and add plugin-legend.xml. See ranger-distro for example files
6. Follow instructions in {ranger folder}/dev-support/ranger-docker/README.md to build ranger and deploy containers