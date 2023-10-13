import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { EChartsOption } from 'echarts';
import { NgxEchartsDirective, NgxEchartsModule } from 'ngx-echarts';
import { combineLatest, map, switchMap } from 'rxjs';
import { HeadingComponent } from '../common-components/heading/heading.component';
import { LoaderComponent } from '../common-components/loader/loader.component';
import { TextComponent } from '../common-components/text/text.component';
import { WeightService } from './weight.service';
import { MeasurementWithUnitPipe } from '../utils/measurement-with-unit.pipe';
import { PercentageDiffColorPipe } from '../utils/percentage-diff-color.pipe';
import { PercentageDiffPipe } from '../utils/percentage-diff.pipe';
import { ActivatedRoute } from '@angular/router';

@Component({
  standalone: true,
  imports: [
    CommonModule,
    HeadingComponent,
    LoaderComponent,
    NgxEchartsModule,
    TextComponent,
    PercentageDiffPipe,
    PercentageDiffColorPipe,
    MeasurementWithUnitPipe,
  ],
  selector: 'app-weight',
  templateUrl: './weight.component.html',
  styleUrls: ['./weight.component.css'],
})
export class WeightComponent {
  readonly initOpts: NgxEchartsDirective['initOpts'] = {
    renderer: 'svg',
  };
  constructor(
    private readonly weightService: WeightService,
    private readonly route: ActivatedRoute
  ) {}
  private readonly $todayWeight = this.weightService.getTodayWeight();
  private readonly $diff = this.route.data.pipe(
    switchMap((data) => this.weightService.getDiff(data['period']))
  );
  private readonly $chartOptions = this.route.data.pipe(
    switchMap((data) =>
      this.weightService.getWeight(data['period']).pipe(
        map(
          (measurements) =>
            ({
              aria: {
                enabled: true,
              },
              animation: false,
              grid: {
                top: 10,
                right: 10,
                bottom: 10,
                left: 10,
              },
              dataset: {
                source: [
                  ['date', 'weight'],
                  ...measurements.map(({ date, weight }) => [
                    new Date(date),
                    weight,
                  ]),
                ],
              },
              xAxis: {
                type: 'time',
                show: false,
              },
              yAxis: {
                max: 'dataMax',
                min: 'dataMin',
                show: false,
              },
              series: [
                {
                  type: 'line',
                  smooth: true,
                  showSymbol: false,
                },
              ],
            } as EChartsOption)
        )
      )
    )
  );

  $vm = combineLatest([this.$todayWeight, this.$diff, this.$chartOptions]).pipe(
    map(([todayWeight, diff, chartOptions]) => ({
      todayWeight,
      diff,
      chartOptions,
    }))
  );
}
