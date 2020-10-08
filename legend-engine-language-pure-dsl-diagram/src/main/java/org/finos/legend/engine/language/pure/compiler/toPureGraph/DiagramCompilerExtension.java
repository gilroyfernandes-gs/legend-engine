// Copyright 2020 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.engine.language.pure.compiler.toPureGraph;

import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.impl.factory.Lists;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.extension.CompilerExtension;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.diagram.Diagram;
import org.finos.legend.pure.generated.Root_meta_pure_metamodel_PackageableElement_Impl;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.PackageableElement;

import java.util.List;

public class DiagramCompilerExtension implements CompilerExtension
{
    @Override
    public List<Function2<org.finos.legend.engine.protocol.pure.v1.model.packageableElement.PackageableElement, CompileContext, PackageableElement>> getExtraPackageableElementFirstPassProcessors()
    {
        return Lists.mutable.with((element, context) ->
        {
            if (element instanceof Diagram)
            {
                Diagram diagram = (Diagram) element;
                // NOTE: we stub out since this element doesn't have an equivalent packageable element form in PURE metamodel
                org.finos.legend.pure.m3.coreinstance.Package pack = context.pureModel.getOrCreatePackage(diagram._package);
                PackageableElement stub = new Root_meta_pure_metamodel_PackageableElement_Impl("")._package(pack)._name(diagram.name);
                pack._childrenAdd(stub);
                diagram.classViews.forEach(view -> HelperDiagramBuilder.processClassView(view, context));
                diagram.propertyViews.forEach(view -> HelperDiagramBuilder.processPropertyView(view, context, diagram));
                diagram.generalizationViews.forEach(view -> HelperDiagramBuilder.processGeneralizationView(view, diagram));
                return stub;
            }
            return null;
        });
    }
}
